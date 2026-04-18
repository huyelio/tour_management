package com.example.tourmanagement.controller;

import com.example.tourmanagement.dto.request.TourRequestDTO;
import com.example.tourmanagement.dto.response.ApiResponse;
import com.example.tourmanagement.dto.response.TourDetailDTO;
import com.example.tourmanagement.dto.response.TourSummaryDTO;
import com.example.tourmanagement.model.enums.TourStatus;
import com.example.tourmanagement.service.TourService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tours")
@RequiredArgsConstructor
public class TourController {

    private final TourService tourService;

    // GET /api/tours?keyword=...&status=...&activeOnly=true
    @GetMapping
    public ResponseEntity<ApiResponse<List<TourSummaryDTO>>> getAllTours(
            @RequestParam(defaultValue = "false") boolean activeOnly,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status
    ) {
        List<TourSummaryDTO> tours;
        if (keyword != null || status != null) {
            TourStatus tourStatus = null;
            if (status != null && !status.isBlank()) {
                try {
                    tourStatus = TourStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException ignored) {
                }
            }
            tours = tourService.searchTours(keyword, tourStatus);
        } else if (activeOnly) {
            tours = tourService.getActiveTours();
        } else {
            tours = tourService.getAllTours();
        }
        return ResponseEntity.ok(ApiResponse.ok("Lấy danh sách tour thành công", tours));
    }

    // GET /api/tours/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TourDetailDTO>> getTourById(@PathVariable Long id) {
        TourDetailDTO tour = tourService.getTourById(id);
        return ResponseEntity.ok(ApiResponse.ok("Lấy chi tiết tour thành công", tour));
    }

    // POST /api/tours
    @PostMapping
    public ResponseEntity<ApiResponse<TourSummaryDTO>> createTour(
            @Valid @RequestBody TourRequestDTO request
    ) {
        TourSummaryDTO created = tourService.createTour(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Tạo tour thành công", created));
    }

    // PUT /api/tours/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TourSummaryDTO>> updateTour(
            @PathVariable Long id,
            @Valid @RequestBody TourRequestDTO request
    ) {
        TourSummaryDTO updated = tourService.updateTour(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật tour thành công", updated));
    }

    // DELETE /api/tours/{id} — cập nhật trạng thái thành CANCELLED (xóa mềm)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelTour(@PathVariable Long id) {
        tourService.cancelTour(id);
        return ResponseEntity.ok(ApiResponse.ok("Hủy tour thành công", null));
    }
}
