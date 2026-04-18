package com.example.tourmanagement.controller;

import com.example.tourmanagement.dto.response.ApiResponse;
import com.example.tourmanagement.dto.response.TourGuideDTO;
import com.example.tourmanagement.model.enums.GuideStatus;
import com.example.tourmanagement.service.TourGuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guides")
@RequiredArgsConstructor
public class TourGuideController {

    private final TourGuideService guideService;

    // GET /api/guides - Lấy tất cả hướng dẫn viên (hỗ trợ lọc)
    @GetMapping
    public ResponseEntity<ApiResponse<List<TourGuideDTO>>> getGuides(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String region
    ) {
        GuideStatus guideStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                guideStatus = GuideStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }

        List<TourGuideDTO> guides = guideService.filterGuides(guideStatus, specialization, language, region);
        return ResponseEntity.ok(ApiResponse.ok("Lấy danh sách hướng dẫn viên thành công", guides));
    }

    // GET /api/guides/{id} - Lấy chi tiết một hướng dẫn viên
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TourGuideDTO>> getGuideById(@PathVariable Long id) {
        TourGuideDTO guide = guideService.getGuideById(id);
        return ResponseEntity.ok(ApiResponse.ok("Lấy thông tin hướng dẫn viên thành công", guide));
    }

    // GET /api/guides/for-tour/{tourId} - Lấy danh sách HDV kèm tính phù hợp cho tour
    // Trả về TẤT CẢ HDV (không lọc theo status), sắp xếp eligible lên trước,
    // kèm field availabilityWarning và eligible để FE hiển thị 2 nhóm.
    @GetMapping("/for-tour/{tourId}")
    public ResponseEntity<ApiResponse<List<TourGuideDTO>>> getGuidesForTour(
            @PathVariable Long tourId,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String region
    ) {
        List<TourGuideDTO> guides = guideService.getGuidesForTour(tourId, specialization, language, region);
        return ResponseEntity.ok(ApiResponse.ok("Lấy danh sách hướng dẫn viên cho tour thành công", guides));
    }
}
