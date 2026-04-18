package com.example.tourmanagement.controller;

import com.example.tourmanagement.dto.request.TourItineraryRequestDTO;
import com.example.tourmanagement.dto.response.ApiResponse;
import com.example.tourmanagement.dto.response.TourItineraryDTO;
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

    // GET /api/tours/{tourId}/itineraries
    @GetMapping
    public ResponseEntity<ApiResponse<List<TourItineraryDTO>>> getItineraries(
            @PathVariable Long tourId
    ) {
        List<TourItineraryDTO> itineraries = itineraryService.getByTourId(tourId);
        return ResponseEntity.ok(ApiResponse.ok("Lấy lịch trình tour thành công", itineraries));
    }

    // POST /api/tours/{tourId}/itineraries
    @PostMapping
    public ResponseEntity<ApiResponse<TourItineraryDTO>> createItinerary(
            @PathVariable Long tourId,
            @Valid @RequestBody TourItineraryRequestDTO request
    ) {
        TourItineraryDTO created = itineraryService.create(tourId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Thêm lịch trình thành công", created));
    }

    // PUT /api/tours/{tourId}/itineraries/{itineraryId}
    @PutMapping("/{itineraryId}")
    public ResponseEntity<ApiResponse<TourItineraryDTO>> updateItinerary(
            @PathVariable Long tourId,
            @PathVariable Long itineraryId,
            @Valid @RequestBody TourItineraryRequestDTO request
    ) {
        TourItineraryDTO updated = itineraryService.update(tourId, itineraryId, request);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật lịch trình thành công", updated));
    }

    // DELETE /api/tours/{tourId}/itineraries/{itineraryId}
    @DeleteMapping("/{itineraryId}")
    public ResponseEntity<ApiResponse<Void>> deleteItinerary(
            @PathVariable Long tourId,
            @PathVariable Long itineraryId
    ) {
        itineraryService.delete(tourId, itineraryId);
        return ResponseEntity.ok(ApiResponse.ok("Xóa lịch trình thành công", null));
    }
}
