package com.example.tourmanagement.service.impl;

import com.example.tourmanagement.dto.response.TourGuideDTO;
import com.example.tourmanagement.exception.ResourceNotFoundException;
import com.example.tourmanagement.model.TourGuide;
import com.example.tourmanagement.model.enums.GuideStatus;
import com.example.tourmanagement.repository.TourGuideRepository;
import com.example.tourmanagement.service.TourGuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TourGuideServiceImpl implements TourGuideService {

    private final TourGuideRepository guideRepository;

    @Override
    public List<TourGuideDTO> getAllGuides() {
        return guideRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TourGuideDTO getGuideById(Long id) {
        TourGuide guide = guideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hướng dẫn viên", id));
        return toDTO(guide);
    }

    @Override
    public List<TourGuideDTO> filterGuides(GuideStatus status, String specialization, String language, String region) {
        return guideRepository.findByFilters(status, specialization, language, region).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private TourGuideDTO toDTO(TourGuide guide) {
        return TourGuideDTO.builder()
                .id(guide.getId())
                .code(guide.getCode())
                .fullName(guide.getFullName())
                .email(guide.getEmail())
                .phone(guide.getPhone())
                .specialization(guide.getSpecialization())
                .languages(guide.getLanguages())
                .region(guide.getRegion())
                .experienceYears(guide.getExperienceYears())
                .status(guide.getStatus())
                .avatarUrl(guide.getAvatarUrl())
                .bio(guide.getBio())
                .build();
    }
}
