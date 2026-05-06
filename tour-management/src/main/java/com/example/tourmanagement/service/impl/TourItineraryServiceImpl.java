package com.example.tourmanagement.service.impl;

import com.example.tourmanagement.exception.BusinessException;
import com.example.tourmanagement.exception.ResourceNotFoundException;
import com.example.tourmanagement.model.Tour;
import com.example.tourmanagement.model.TourItinerary;
import com.example.tourmanagement.model.enums.TourStatus;
import com.example.tourmanagement.repository.TourItineraryRepository;
import com.example.tourmanagement.service.TourItineraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TourItineraryServiceImpl implements TourItineraryService {

    private final TourItineraryRepository itineraryRepository;

    @Override
    public List<TourItinerary> getByTourId(Tour tour) {
        Long tourId = tour.getId();
        if (tourId == null) {
            throw new BusinessException("Tour cần có ID");
        }
        return itineraryRepository.findByTourIdSorted(tourId);
    }

    @Override
    @Transactional
    public TourItinerary create(Tour tour, TourItinerary itinerary) {
        validateTourEditable(tour);
        itinerary.setTour(tour);
        Long tourId = tour.getId();
        validateDayNumber(tourId, itinerary.getDayNumber(), itinerary.getSequenceOrder(), tour.getDurationDays(), null);
        validateTime(itinerary.getStartTime(), itinerary.getEndTime());
        itinerary.setTitle(itinerary.getTitle().trim());

        return itineraryRepository.save(itinerary);
    }

    @Override
    @Transactional
    public TourItinerary update(Tour tour, TourItinerary itinerary) {
        validateTourEditable(tour);

        Long tourId = tour.getId();
        Long itineraryId = itinerary.getId();
        if (tourId == null || itineraryId == null) {
            throw new BusinessException("Tour và lịch trình cần có ID");
        }

        TourItinerary managed = itineraryRepository.findById(itineraryId)
                .orElseThrow(() -> new ResourceNotFoundException("Lịch trình", itineraryId));

        if (!managed.getTour().getId().equals(tourId)) {
            throw new BusinessException("Lịch trình không thuộc tour này");
        }

        validateDayNumber(tourId, itinerary.getDayNumber(), itinerary.getSequenceOrder(), tour.getDurationDays(), itineraryId);
        validateTime(itinerary.getStartTime(), itinerary.getEndTime());

        managed.setDayNumber(itinerary.getDayNumber());
        managed.setSequenceOrder(itinerary.getSequenceOrder());
        managed.setTitle(itinerary.getTitle().trim());
        managed.setDescription(itinerary.getDescription());
        managed.setLocation(itinerary.getLocation());
        managed.setStartTime(itinerary.getStartTime());
        managed.setEndTime(itinerary.getEndTime());
        managed.setActivityType(itinerary.getActivityType());
        managed.setNote(itinerary.getNote());
        managed.setIsOptional(itinerary.getIsOptional() != null ? itinerary.getIsOptional() : false);

        return itineraryRepository.save(managed);
    }

    @Override
    @Transactional
    public void delete(Tour tour, TourItinerary itinerary) {
        validateTourEditable(tour);

        Long tourId = tour.getId();
        Long itineraryId = itinerary.getId();
        if (tourId == null || itineraryId == null) {
            throw new BusinessException("Tour và lịch trình cần có ID");
        }

        TourItinerary managed = itineraryRepository.findById(itineraryId)
                .orElseThrow(() -> new ResourceNotFoundException("Lịch trình", itineraryId));

        if (!managed.getTour().getId().equals(tourId)) {
            throw new BusinessException("Lịch trình không thuộc tour này");
        }

        itineraryRepository.delete(managed);
    }

    private void validateTourEditable(Tour tour) {
        if (tour.getStatus() == TourStatus.COMPLETED) {
            throw new BusinessException("Không thể thay đổi lịch trình của tour đã hoàn thành");
        }
        if (tour.getStatus() == TourStatus.CANCELLED) {
            throw new BusinessException("Không thể thay đổi lịch trình của tour đã bị hủy");
        }
    }

    private void validateDayNumber(
            Long tourId,
            Integer dayNumber,
            Integer sequenceOrder,
            Integer durationDays,
            Long excludeItineraryId
    ) {
        if (durationDays == null || durationDays <= 0) return;
        if (dayNumber > durationDays) {
            throw new BusinessException(
                "Ngày " + dayNumber + " vượt quá số ngày của tour (" + durationDays + " ngày)");
        }

        boolean duplicatedOrder = isDuplicatedSequenceOrder(
                tourId, dayNumber, sequenceOrder, excludeItineraryId);

        if (duplicatedOrder) {
            throw new BusinessException(
                    "Trong ngày " + dayNumber + " đã tồn tại hoạt động ở thứ tự " + sequenceOrder);
        }
    }

    private boolean isDuplicatedSequenceOrder(
            Long tourId,
            Integer dayNumber,
            Integer sequenceOrder,
            Long excludeItineraryId
    ) {
        if (excludeItineraryId == null) {
            return itineraryRepository.existsByTourIdAndDayNumberAndSequenceOrder(
                    tourId, dayNumber, sequenceOrder);
        }

        return itineraryRepository.existsByTourIdAndDayNumberAndSequenceOrderAndIdNot(
                tourId, dayNumber, sequenceOrder, excludeItineraryId);
    }

    private void validateTime(LocalTime startTime, LocalTime endTime) {
        if (startTime != null && endTime != null
                && !startTime.isBefore(endTime)) {
            throw new BusinessException("Giờ bắt đầu phải trước giờ kết thúc");
        }
    }
}
