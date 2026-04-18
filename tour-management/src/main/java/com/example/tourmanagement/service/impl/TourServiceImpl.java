package com.example.tourmanagement.service.impl;

import com.example.tourmanagement.dto.request.TourRequestDTO;
import com.example.tourmanagement.dto.response.AssignmentDTO;
import com.example.tourmanagement.dto.response.TourDetailDTO;
import com.example.tourmanagement.dto.response.TourItineraryDTO;
import com.example.tourmanagement.dto.response.TourSummaryDTO;
import com.example.tourmanagement.exception.BusinessException;
import com.example.tourmanagement.exception.ResourceNotFoundException;
import com.example.tourmanagement.model.Tour;
import com.example.tourmanagement.model.TourAssignment;
import com.example.tourmanagement.model.TourItinerary;
import com.example.tourmanagement.model.enums.TourStatus;
import com.example.tourmanagement.repository.TourAssignmentRepository;
import com.example.tourmanagement.repository.TourItineraryRepository;
import com.example.tourmanagement.repository.TourRepository;
import com.example.tourmanagement.service.TourService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TourServiceImpl implements TourService {

    private final TourRepository tourRepository;
    private final TourAssignmentRepository assignmentRepository;
    private final TourItineraryRepository itineraryRepository;

    @Override
    public List<TourSummaryDTO> getAllTours() {
        return tourRepository.findAll().stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TourSummaryDTO> getActiveTours() {
        return tourRepository.findActiveTours().stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TourSummaryDTO> searchTours(String keyword, TourStatus status) {
        return tourRepository.searchTours(keyword, status).stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TourDetailDTO getTourById(Long id) {
        Tour tour = tourRepository.findByIdWithAssignments(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tour", id));
        List<TourItineraryDTO> itineraries = itineraryRepository
                .findByTourIdSorted(id)
                .stream()
                .map(this::toItineraryDTO)
                .collect(Collectors.toList());
        return toDetailDTO(tour, itineraries);
    }

    @Override
    @Transactional
    public TourSummaryDTO createTour(TourRequestDTO request) {
        String code = request.getCode().trim().toUpperCase();
        if (tourRepository.existsByCode(code)) {
            throw new BusinessException("Mã tour '" + code + "' đã tồn tại");
        }
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BusinessException("Ngày kết thúc phải >= ngày bắt đầu");
        }
        int durationDays = (int) ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;

        Tour tour = Tour.builder()
                .code(code)
                .name(request.getName().trim())
                .description(request.getDescription())
                .destination(request.getDestination().trim())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .durationDays(durationDays)
                .maxGuests(request.getMaxGuests())
                .currentGuests(0)
                .price(request.getPrice())
                .status(request.getStatus() != null ? request.getStatus() : TourStatus.PLANNING)
                .minGuides(request.getMinGuides() != null ? request.getMinGuides() : 1)
                .requiredLanguages(request.getRequiredLanguages())
                .requiredSpecialization(request.getRequiredSpecialization())
                .departureRegion(request.getDepartureRegion())
                .build();

        return toSummaryDTO(tourRepository.save(tour));
    }

    @Override
    @Transactional
    public TourSummaryDTO updateTour(Long id, TourRequestDTO request) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tour", id));

        if (tour.getStatus() == TourStatus.COMPLETED) {
            throw new BusinessException("Không thể chỉnh sửa tour đã hoàn thành");
        }

        String code = request.getCode().trim().toUpperCase();
        if (!tour.getCode().equalsIgnoreCase(code) && tourRepository.existsByCodeAndIdNot(code, id)) {
            throw new BusinessException("Mã tour '" + code + "' đã tồn tại");
        }
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BusinessException("Ngày kết thúc phải >= ngày bắt đầu");
        }
        int durationDays = (int) ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;

        tour.setCode(code);
        tour.setName(request.getName().trim());
        tour.setDescription(request.getDescription());
        tour.setDestination(request.getDestination().trim());
        tour.setStartDate(request.getStartDate());
        tour.setEndDate(request.getEndDate());
        tour.setDurationDays(durationDays);
        tour.setMaxGuests(request.getMaxGuests());
        tour.setPrice(request.getPrice());
        if (request.getStatus() != null) {
            tour.setStatus(request.getStatus());
        }
        tour.setMinGuides(request.getMinGuides() != null ? request.getMinGuides() : 1);
        tour.setRequiredLanguages(request.getRequiredLanguages());
        tour.setRequiredSpecialization(request.getRequiredSpecialization());
        tour.setDepartureRegion(request.getDepartureRegion());

        return toSummaryDTO(tourRepository.save(tour));
    }

    @Override
    @Transactional
    public void cancelTour(Long id) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tour", id));
        if (tour.getStatus() == TourStatus.CANCELLED) {
            throw new BusinessException("Tour đã bị hủy trước đó");
        }
        if (tour.getStatus() == TourStatus.COMPLETED) {
            throw new BusinessException("Không thể hủy tour đã hoàn thành");
        }
        tour.setStatus(TourStatus.CANCELLED);
        tourRepository.save(tour);
    }

    private TourSummaryDTO toSummaryDTO(Tour tour) {
        long count = assignmentRepository.countActiveAssignmentsByTourId(tour.getId());
        return TourSummaryDTO.builder()
                .id(tour.getId())
                .code(tour.getCode())
                .name(tour.getName())
                .destination(tour.getDestination())
                .startDate(tour.getStartDate())
                .endDate(tour.getEndDate())
                .durationDays(tour.getDurationDays())
                .maxGuests(tour.getMaxGuests())
                .currentGuests(tour.getCurrentGuests())
                .price(tour.getPrice())
                .status(tour.getStatus())
                .minGuides(tour.getMinGuides())
                .assignedGuideCount(count)
                .build();
    }

    private TourDetailDTO toDetailDTO(Tour tour, List<TourItineraryDTO> itineraries) {
        List<AssignmentDTO> assignmentDTOs = tour.getAssignments().stream()
                .map(this::toAssignmentDTO)
                .collect(Collectors.toList());

        return TourDetailDTO.builder()
                .id(tour.getId())
                .code(tour.getCode())
                .name(tour.getName())
                .description(tour.getDescription())
                .destination(tour.getDestination())
                .startDate(tour.getStartDate())
                .endDate(tour.getEndDate())
                .durationDays(tour.getDurationDays())
                .maxGuests(tour.getMaxGuests())
                .currentGuests(tour.getCurrentGuests())
                .price(tour.getPrice())
                .status(tour.getStatus())
                .requiredLanguages(tour.getRequiredLanguages())
                .requiredSpecialization(tour.getRequiredSpecialization())
                .minGuides(tour.getMinGuides())
                .departureRegion(tour.getDepartureRegion())
                .assignments(assignmentDTOs)
                .itineraries(itineraries)
                .build();
    }

    private TourItineraryDTO toItineraryDTO(TourItinerary it) {
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

    private AssignmentDTO toAssignmentDTO(TourAssignment a) {
        return AssignmentDTO.builder()
                .id(a.getId())
                .tourId(a.getTour().getId())
                .tourCode(a.getTour().getCode())
                .tourName(a.getTour().getName())
                .guideId(a.getGuide().getId())
                .guideCode(a.getGuide().getCode())
                .guideName(a.getGuide().getFullName())
                .guidePhone(a.getGuide().getPhone())
                .guideLanguages(a.getGuide().getLanguages())
                .role(a.getRole())
                .note(a.getNote())
                .status(a.getStatus())
                .assignedAt(a.getAssignedAt())
                .assignedBy(a.getAssignedBy())
                .build();
    }
}
