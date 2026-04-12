package com.example.tourmanagement.service;

import com.example.tourmanagement.dto.response.TourDetailDTO;
import com.example.tourmanagement.dto.response.TourSummaryDTO;

import java.util.List;

public interface TourService {
    List<TourSummaryDTO> getAllTours();
    List<TourSummaryDTO> getActiveTours();
    TourDetailDTO getTourById(Long id);
}
