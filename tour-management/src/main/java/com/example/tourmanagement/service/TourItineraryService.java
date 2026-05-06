package com.example.tourmanagement.service;

import com.example.tourmanagement.model.Tour;
import com.example.tourmanagement.model.TourItinerary;

import java.util.List;

public interface TourItineraryService {
    List<TourItinerary> getByTourId(Tour tour);
    TourItinerary create(Tour tour, TourItinerary itinerary);
    TourItinerary update(Tour tour, TourItinerary itinerary);
    void delete(Tour tour, TourItinerary itinerary);
}
