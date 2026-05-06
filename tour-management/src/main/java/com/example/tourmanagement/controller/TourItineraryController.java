package com.example.tourmanagement.controller;

import com.example.tourmanagement.controller.support.TourEntityLoader;
import com.example.tourmanagement.dto.response.ApiResponse;
import com.example.tourmanagement.model.Tour;
import com.example.tourmanagement.model.TourItinerary;
import com.example.tourmanagement.service.TourItineraryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tours/{tourId}/itineraries")
@RequiredArgsConstructor
public class TourItineraryController {

    private final TourItineraryService itineraryService;
    private final TourEntityLoader tourLoader;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TourItinerary>>> getItineraries(
            @PathVariable Long tourId
    ) {
        Tour tour = tourLoader.requireById(tourId);
        List<TourItinerary> itineraries = itineraryService.getByTourId(tour);
        return ResponseEntity.ok(ApiResponse.ok("Lấy lịch trình tour thành công", itineraries));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TourItinerary>> createItinerary(
            @PathVariable Long tourId,
            @Valid @RequestBody TourItinerary itinerary
    ) {
        Tour tour = tourLoader.requireById(tourId);
        itinerary.setId(null);
        TourItinerary created = itineraryService.create(tour, itinerary);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Thêm lịch trình thành công", created));
    }

    @PutMapping("/{itineraryId}")
    public ResponseEntity<ApiResponse<TourItinerary>> updateItinerary(
            @PathVariable Long tourId,
            @PathVariable Long itineraryId,
            @Valid @RequestBody TourItinerary itinerary
    ) {
        Tour tour = tourLoader.requireById(tourId);
        itinerary.setId(itineraryId);
        TourItinerary updated = itineraryService.update(tour, itinerary);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật lịch trình thành công", updated));
    }

    @DeleteMapping("/{itineraryId}")
    public ResponseEntity<ApiResponse<Void>> deleteItinerary(
            @PathVariable Long tourId,
            @PathVariable Long itineraryId
    ) {
        Tour tour = tourLoader.requireById(tourId);
        TourItinerary itinerary = TourItinerary.builder().id(itineraryId).build();
        itineraryService.delete(tour, itinerary);
        return ResponseEntity.ok(ApiResponse.ok("Xóa lịch trình thành công", null));
    }
}
