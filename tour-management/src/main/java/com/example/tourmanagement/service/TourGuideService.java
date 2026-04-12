package com.example.tourmanagement.service;

import com.example.tourmanagement.dto.response.TourGuideDTO;
import com.example.tourmanagement.model.enums.GuideStatus;

import java.util.List;

public interface TourGuideService {
    List<TourGuideDTO> getAllGuides();
    TourGuideDTO getGuideById(Long id);
    List<TourGuideDTO> filterGuides(GuideStatus status, String specialization, String language, String region);
}
