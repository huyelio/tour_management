package com.example.tourmanagement.controller;

import com.example.tourmanagement.controller.support.TourEntityLoader;
import com.example.tourmanagement.dto.response.ApiResponse;
import com.example.tourmanagement.model.Tour;
import com.example.tourmanagement.model.enums.TourStatus;
import com.example.tourmanagement.model.json.TourJsonViews;
import com.example.tourmanagement.service.TourService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/tours")
@RequiredArgsConstructor
public class TourController {

    private final TourService tourService;
    private final TourEntityLoader tourLoader;

    @GetMapping
    public ResponseEntity<MappingJacksonValue> getAllTours(
            @RequestParam(defaultValue = "false") boolean activeOnly,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status
    ) {
        TourStatus tourStatus = parseOptionalTourStatus(status);
        boolean hasFilter = (keyword != null && !keyword.isBlank()) || tourStatus != null;

        List<Tour> tours;
        if (hasFilter) {
            tours = tourService.searchTours(
                    keyword != null && !keyword.isBlank() ? keyword.trim() : null,
                    tourStatus
            );
        } else if (activeOnly) {
            tours = tourService.getActiveTours();
        } else {
            tours = tourService.getAllTours();
        }

        MappingJacksonValue wrapped = new MappingJacksonValue(
                ApiResponse.ok("Lấy danh sách tour thành công", tours));
        wrapped.setSerializationView(TourJsonViews.ListItem.class);
        return ResponseEntity.ok(wrapped);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MappingJacksonValue> getTourById(@PathVariable Long id) {
        Tour tour = tourService.getTourById(id);
        MappingJacksonValue wrapped = new MappingJacksonValue(
                ApiResponse.ok("Lấy chi tiết tour thành công", tour));
        wrapped.setSerializationView(TourJsonViews.Detail.class);
        return ResponseEntity.ok(wrapped);
    }

    @PostMapping
    public ResponseEntity<MappingJacksonValue> createTour(@Valid @RequestBody Tour tour) {
        tour.setId(null);
        tour.setAssignedGuideCount(null);
        tour.setAssignments(new ArrayList<>());
        tour.setBookings(new ArrayList<>());
        tour.setItineraries(new ArrayList<>());
        Tour created = tourService.createTour(tour);
        MappingJacksonValue wrapped = new MappingJacksonValue(
                ApiResponse.ok("Tạo tour thành công", created));
        wrapped.setSerializationView(TourJsonViews.ListItem.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(wrapped);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MappingJacksonValue> updateTour(
            @PathVariable Long id,
            @Valid @RequestBody Tour tour
    ) {
        tour.setId(id);
        tour.setAssignedGuideCount(null);
        tour.setAssignments(null);
        tour.setBookings(null);
        tour.setItineraries(null);
        Tour updated = tourService.updateTour(tour);
        MappingJacksonValue wrapped = new MappingJacksonValue(
                ApiResponse.ok("Cập nhật tour thành công", updated));
        wrapped.setSerializationView(TourJsonViews.ListItem.class);
        return ResponseEntity.ok(wrapped);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelTour(@PathVariable Long id) {
        Tour tour = tourLoader.requireById(id);
        tourService.cancelTour(tour);
        return ResponseEntity.ok(ApiResponse.ok("Hủy tour thành công", null));
    }

    private static TourStatus parseOptionalTourStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return TourStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
