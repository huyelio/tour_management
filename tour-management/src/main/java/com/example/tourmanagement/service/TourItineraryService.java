package com.example.tourmanagement.service;

import com.example.tourmanagement.dto.request.TourItineraryRequestDTO;
import com.example.tourmanagement.dto.response.TourItineraryDTO;

import java.util.List;

public interface TourItineraryService {
    List<TourItineraryDTO> getByTourId(Long tourId);
    TourItineraryDTO create(Long tourId, TourItineraryRequestDTO request);
    TourItineraryDTO update(Long tourId, Long itineraryId, TourItineraryRequestDTO request);
    void delete(Long tourId, Long itineraryId);
}
