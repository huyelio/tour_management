package com.example.tourmanagement.controller;

import com.example.tourmanagement.dto.response.ApiResponse;
import com.example.tourmanagement.dto.response.TourDetailDTO;
import com.example.tourmanagement.dto.response.TourSummaryDTO;
import com.example.tourmanagement.service.TourService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tours")
@RequiredArgsConstructor
public class TourController {

    private final TourService tourService;

    // GET /api/tours - Lấy tất cả tour
    @GetMapping
    public ResponseEntity<ApiResponse<List<TourSummaryDTO>>> getAllTours(
            @RequestParam(defaultValue = "false") boolean activeOnly
    ) {
        List<TourSummaryDTO> tours = activeOnly
                ? tourService.getActiveTours()
                : tourService.getAllTours();
        return ResponseEntity.ok(ApiResponse.ok("Lấy danh sách tour thành công", tours));
    }

    // GET /api/tours/{id} - Lấy chi tiết tour (kèm danh sách phân công)
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TourDetailDTO>> getTourById(@PathVariable Long id) {
        TourDetailDTO tour = tourService.getTourById(id);
        return ResponseEntity.ok(ApiResponse.ok("Lấy chi tiết tour thành công", tour));
    }
}
