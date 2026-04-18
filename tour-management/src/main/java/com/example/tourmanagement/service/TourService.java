package com.example.tourmanagement.service;

import com.example.tourmanagement.dto.request.TourRequestDTO;
import com.example.tourmanagement.dto.response.TourDetailDTO;
import com.example.tourmanagement.dto.response.TourSummaryDTO;
import com.example.tourmanagement.model.enums.TourStatus;

import java.util.List;

public interface TourService {
    List<TourSummaryDTO> getAllTours();
    List<TourSummaryDTO> getActiveTours();
    List<TourSummaryDTO> searchTours(String keyword, TourStatus status);
    TourDetailDTO getTourById(Long id);
    TourSummaryDTO createTour(TourRequestDTO request);
    TourSummaryDTO updateTour(Long id, TourRequestDTO request);
    void cancelTour(Long id);
}
