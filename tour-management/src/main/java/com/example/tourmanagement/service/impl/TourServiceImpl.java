package com.example.tourmanagement.service.impl;

import com.example.tourmanagement.exception.BusinessException;
import com.example.tourmanagement.exception.ResourceNotFoundException;
import com.example.tourmanagement.model.Tour;
import com.example.tourmanagement.model.TourAssignment;
import com.example.tourmanagement.model.enums.TourStatus;
import com.example.tourmanagement.repository.TourAssignmentRepository;
import com.example.tourmanagement.repository.TourRepository;
import com.example.tourmanagement.service.TourService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TourServiceImpl implements TourService {

    private final TourRepository tourRepository;
    private final TourAssignmentRepository assignmentRepository;

    @Override
    public List<Tour> getAllTours() {
        return tourRepository.findAll().stream()
                .map(this::withAssignedGuideCount)
                .collect(Collectors.toList());
    }

    @Override
    public List<Tour> getActiveTours() {
        return tourRepository.findActiveTours().stream()
                .map(this::withAssignedGuideCount)
                .collect(Collectors.toList());
    }

    @Override
    public List<Tour> searchTours(String keyword, TourStatus status) {
        return tourRepository.searchTours(keyword, status).stream()
                .map(this::withAssignedGuideCount)
                .collect(Collectors.toList());
    }

    @Override
    public Tour getTourById(Long id) {
        Tour tour = tourRepository.findByIdWithAssignments(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tour", id));
        tour.getAssignments().sort(Comparator.comparing(
                TourAssignment::getId,
                Comparator.nullsFirst(Long::compareTo)));
        return withAssignedGuideCount(tour);
    }

    @Override
    @Transactional
    public Tour createTour(Tour tour) {
        String code = tour.getCode().trim().toUpperCase();
        tour.setCode(code);
        tour.setName(tour.getName().trim());
        tour.setDestination(tour.getDestination().trim());
        if (tourRepository.existsByCode(code)) {
            throw new BusinessException("Mã tour '" + code + "' đã tồn tại");
        }
        if (tour.getEndDate().isBefore(tour.getStartDate())) {
            throw new BusinessException("Ngày kết thúc phải >= ngày bắt đầu");
        }
        int durationDays = (int) ChronoUnit.DAYS.between(tour.getStartDate(), tour.getEndDate()) + 1;
        tour.setDurationDays(durationDays);
        if (tour.getStatus() == null) {
            tour.setStatus(TourStatus.PLANNING);
        }
        if (tour.getMinGuides() == null) {
            tour.setMinGuides(1);
        }

        return withAssignedGuideCount(tourRepository.save(tour));
    }

    @Override
    @Transactional
    public Tour updateTour(Tour tour) {
        if (tour.getId() == null) {
            throw new BusinessException("Tour cần có ID để cập nhật");
        }

        Tour existing = tourRepository.findById(tour.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tour", tour.getId()));

        if (existing.getStatus() == TourStatus.COMPLETED) {
            throw new BusinessException("Không thể chỉnh sửa tour đã hoàn thành");
        }

        String code = tour.getCode().trim().toUpperCase();
        if (!existing.getCode().equalsIgnoreCase(code) && tourRepository.existsByCodeAndIdNot(code, tour.getId())) {
            throw new BusinessException("Mã tour '" + code + "' đã tồn tại");
        }
        if (tour.getEndDate().isBefore(tour.getStartDate())) {
            throw new BusinessException("Ngày kết thúc phải >= ngày bắt đầu");
        }
        int durationDays = (int) ChronoUnit.DAYS.between(tour.getStartDate(), tour.getEndDate()) + 1;

        existing.setCode(code);
        existing.setName(tour.getName().trim());
        existing.setDescription(tour.getDescription());
        existing.setDestination(tour.getDestination().trim());
        existing.setStartDate(tour.getStartDate());
        existing.setEndDate(tour.getEndDate());
        existing.setDurationDays(durationDays);
        existing.setMaxGuests(tour.getMaxGuests());
        existing.setPrice(tour.getPrice());
        if (tour.getStatus() != null) {
            existing.setStatus(tour.getStatus());
        }
        existing.setMinGuides(tour.getMinGuides() != null ? tour.getMinGuides() : 1);
        existing.setRequiredLanguages(tour.getRequiredLanguages());
        existing.setRequiredSpecialization(tour.getRequiredSpecialization());
        existing.setDepartureRegion(tour.getDepartureRegion());

        return withAssignedGuideCount(tourRepository.save(existing));
    }

    @Override
    @Transactional
    public void cancelTour(Tour tour) {
        if (tour.getStatus() == TourStatus.CANCELLED) {
            throw new BusinessException("Tour đã bị hủy trước đó");
        }
        if (tour.getStatus() == TourStatus.COMPLETED) {
            throw new BusinessException("Không thể hủy tour đã hoàn thành");
        }
        tour.setStatus(TourStatus.CANCELLED);
        tourRepository.save(tour);
    }

    private Tour withAssignedGuideCount(Tour tour) {
        long count = assignmentRepository.countActiveAssignmentsByTourId(tour.getId());
        tour.setAssignedGuideCount(count);
        return tour;
    }
}
