package com.example.tourmanagement.service.impl;

import com.example.tourmanagement.exception.BusinessException;
import com.example.tourmanagement.exception.ResourceNotFoundException;
import com.example.tourmanagement.model.Tour;
import com.example.tourmanagement.model.TourGuide;
import com.example.tourmanagement.model.enums.GuideStatus;
import com.example.tourmanagement.repository.TourGuideRepository;
import com.example.tourmanagement.service.TourGuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TourGuideServiceImpl implements TourGuideService {

    private final TourGuideRepository guideRepository;

    @Override
    public List<TourGuide> getAllGuides() {
        return guideRepository.findAll();
    }

    @Override
    public TourGuide getGuideById(Long id) {
        return guideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hướng dẫn viên", id));
    }

    @Override
    public List<TourGuide> filterGuides(GuideStatus status, String specialization, String language, String region) {
        return guideRepository.findByFilters(status, specialization, language, region);
    }

    @Override
    public List<TourGuide> getGuidesForTour(Tour tour) {
        Long tourId = tour.getId();
        if (tourId == null) {
            throw new BusinessException("Tour cần có ID");
        }

        Set<Long> overlappingIds = new HashSet<>(
                guideRepository.findGuideIdsWithScheduleOverlap(tour.getStartDate(), tour.getEndDate(), tourId)
        );

        return guideRepository.findAll().stream()
                .map(g -> copyWithTourHints(g, overlappingIds))
                .sorted(Comparator
                        .comparing((TourGuide g) -> Boolean.TRUE.equals(g.getEligible()))
                        .reversed()
                        .thenComparing(TourGuide::getFullName, Comparator.nullsLast(String::compareToIgnoreCase)))
                .collect(Collectors.toList());
    }

    /** Bản sao tách persistence context — tránh ghi @Transient lên entity đang managed. */
    private static TourGuide copyWithTourHints(TourGuide src, Set<Long> overlappingIds) {
        String warning = null;
        if (src.getStatus() == GuideStatus.INACTIVE) {
            warning = "Hướng dẫn viên không hoạt động";
        } else if (src.getStatus() == GuideStatus.ON_LEAVE) {
            warning = "Hướng dẫn viên đang nghỉ phép";
        } else if (overlappingIds.contains(src.getId())) {
            warning = "Trùng lịch với tour khác";
        }
        TourGuide g = TourGuide.builder()
                .id(src.getId())
                .code(src.getCode())
                .fullName(src.getFullName())
                .email(src.getEmail())
                .phone(src.getPhone())
                .dateOfBirth(src.getDateOfBirth())
                .specialization(src.getSpecialization())
                .languages(src.getLanguages())
                .region(src.getRegion())
                .experienceYears(src.getExperienceYears())
                .status(src.getStatus())
                .avatarUrl(src.getAvatarUrl())
                .bio(src.getBio())
                .build();
        g.setAvailabilityWarning(warning);
        g.setEligible(warning == null);
        return g;
    }
}
