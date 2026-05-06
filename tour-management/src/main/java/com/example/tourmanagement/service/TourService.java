package com.example.tourmanagement.service;

import com.example.tourmanagement.model.Tour;
import com.example.tourmanagement.model.enums.TourStatus;

import java.util.List;

public interface TourService {
    List<Tour> getAllTours();
    List<Tour> getActiveTours();
    List<Tour> searchTours(String keyword, TourStatus status);
    Tour getTourById(Long id);
    Tour createTour(Tour tour);
    Tour updateTour(Tour tour);
    void cancelTour(Tour tour);
}
