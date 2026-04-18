package com.example.tourmanagement.service.impl;

import com.example.tourmanagement.dto.request.TourItineraryRequestDTO;
import com.example.tourmanagement.dto.response.TourItineraryDTO;
import com.example.tourmanagement.exception.BusinessException;
import com.example.tourmanagement.exception.ResourceNotFoundException;
import com.example.tourmanagement.model.Tour;
import com.example.tourmanagement.model.TourItinerary;
import com.example.tourmanagement.model.enums.TourStatus;
import com.example.tourmanagement.repository.TourItineraryRepository;
import com.example.tourmanagement.repository.TourRepository;
import com.example.tourmanagement.service.TourItineraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TourItineraryServiceImpl implements TourItineraryService {

    private final TourItineraryRepository itineraryRepository;
    private final TourRepository tourRepository;

    @Override
    public List<TourItineraryDTO> getByTourId(Long tourId) {
        if (!tourRepository.existsById(tourId)) {
            throw new ResourceNotFoundException("Tour", tourId);
        }
        return itineraryRepository.findByTourIdSorted(tourId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TourItineraryDTO create(Long tourId, TourItineraryRequestDTO request) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ResourceNotFoundException("Tour", tourId));

        validateTourEditable(tour);
        validateDayNumber(request.getDayNumber(), tour.getDurationDays());
        validateTime(request);

        TourItinerary itinerary = TourItinerary.builder()
                .tour(tour)
                .dayNumber(request.getDayNumber())
                .sequenceOrder(request.getSequenceOrder())
                .title(request.getTitle().trim())
                .description(request.getDescription())
                .location(request.getLocation())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .activityType(request.getActivityType())
                .note(request.getNote())
                .isOptional(request.getIsOptional() != null ? request.getIsOptional() : false)
                .build();

        return toDTO(itineraryRepository.save(itinerary));
    }

    @Override
    @Transactional
    public TourItineraryDTO update(Long tourId, Long itineraryId, TourItineraryRequestDTO request) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ResourceNotFoundException("Tour", tourId));

        validateTourEditable(tour);

        TourItinerary itinerary = itineraryRepository.findById(itineraryId)
                .orElseThrow(() -> new ResourceNotFoundException("Lịch trình", itineraryId));

        if (!itinerary.getTour().getId().equals(tourId)) {
            throw new BusinessException("Lịch trình không thuộc tour này");
        }

        validateDayNumber(request.getDayNumber(), tour.getDurationDays());
        validateTime(request);

        itinerary.setDayNumber(request.getDayNumber());
        itinerary.setSequenceOrder(request.getSequenceOrder());
        itinerary.setTitle(request.getTitle().trim());
        itinerary.setDescription(request.getDescription());
        itinerary.setLocation(request.getLocation());
        itinerary.setStartTime(request.getStartTime());
        itinerary.setEndTime(request.getEndTime());
        itinerary.setActivityType(request.getActivityType());
        itinerary.setNote(request.getNote());
        itinerary.setIsOptional(request.getIsOptional() != null ? request.getIsOptional() : false);

        return toDTO(itineraryRepository.save(itinerary));
    }

    @Override
    @Transactional
    public void delete(Long tourId, Long itineraryId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ResourceNotFoundException("Tour", tourId));

        validateTourEditable(tour);

        TourItinerary itinerary = itineraryRepository.findById(itineraryId)
                .orElseThrow(() -> new ResourceNotFoundException("Lịch trình", itineraryId));

        if (!itinerary.getTour().getId().equals(tourId)) {
            throw new BusinessException("Lịch trình không thuộc tour này");
        }

        itineraryRepository.delete(itinerary);
    }

    private void validateTourEditable(Tour tour) {
        if (tour.getStatus() == TourStatus.COMPLETED) {
            throw new BusinessException("Không thể thay đổi lịch trình của tour đã hoàn thành");
        }
        if (tour.getStatus() == TourStatus.CANCELLED) {
            throw new BusinessException("Không thể thay đổi lịch trình của tour đã bị hủy");
        }
    }

    private void validateDayNumber(Integer dayNumber, Integer durationDays) {
        if (durationDays == null || durationDays <= 0) return;
        if (dayNumber > durationDays) {
            throw new BusinessException(
                "Ngày " + dayNumber + " vượt quá số ngày của tour (" + durationDays + " ngày)");
        }
    }

    private void validateTime(TourItineraryRequestDTO request) {
        if (request.getStartTime() != null && request.getEndTime() != null
                && !request.getStartTime().isBefore(request.getEndTime())) {
            throw new BusinessException("Giờ bắt đầu phải trước giờ kết thúc");
        }
    }

    private TourItineraryDTO toDTO(TourItinerary it) {
        return TourItineraryDTO.builder()
                .id(it.getId())
                .tourId(it.getTour().getId())
                .dayNumber(it.getDayNumber())
                .sequenceOrder(it.getSequenceOrder())
                .title(it.getTitle())
                .description(it.getDescription())
                .location(it.getLocation())
                .startTime(it.getStartTime())
                .endTime(it.getEndTime())
                .activityType(it.getActivityType())
                .note(it.getNote())
                .isOptional(it.getIsOptional())
                .build();
    }
}
