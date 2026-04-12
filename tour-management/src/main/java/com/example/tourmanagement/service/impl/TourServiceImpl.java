package com.example.tourmanagement.service.impl;

import com.example.tourmanagement.dto.response.AssignmentDTO;
import com.example.tourmanagement.dto.response.TourDetailDTO;
import com.example.tourmanagement.dto.response.TourSummaryDTO;
import com.example.tourmanagement.exception.ResourceNotFoundException;
import com.example.tourmanagement.model.Tour;
import com.example.tourmanagement.model.TourAssignment;
import com.example.tourmanagement.repository.TourAssignmentRepository;
import com.example.tourmanagement.repository.TourRepository;
import com.example.tourmanagement.service.TourService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TourServiceImpl implements TourService {

    private final TourRepository tourRepository;
    private final TourAssignmentRepository assignmentRepository;

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
    public TourDetailDTO getTourById(Long id) {
        Tour tour = tourRepository.findByIdWithAssignments(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tour", id));
        return toDetailDTO(tour);
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

    private TourDetailDTO toDetailDTO(Tour tour) {
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
