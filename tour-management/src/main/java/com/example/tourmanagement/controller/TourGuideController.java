package com.example.tourmanagement.controller;

import com.example.tourmanagement.controller.support.TourEntityLoader;
import com.example.tourmanagement.dto.response.ApiResponse;
import com.example.tourmanagement.model.Tour;
import com.example.tourmanagement.model.TourGuide;
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
    private final TourEntityLoader tourLoader;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TourGuide>>> getGuides(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String region
    ) {
        GuideStatus guideStatus = parseOptionalGuideStatus(status);
        List<TourGuide> guides = guideService.filterGuides(guideStatus, specialization, language, region);
        return ResponseEntity.ok(ApiResponse.ok("Lấy danh sách hướng dẫn viên thành công", guides));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TourGuide>> getGuideById(@PathVariable Long id) {
        TourGuide guide = guideService.getGuideById(id);
        return ResponseEntity.ok(ApiResponse.ok("Lấy thông tin hướng dẫn viên thành công", guide));
    }

    @GetMapping("/for-tour/{tourId}")
    public ResponseEntity<ApiResponse<List<TourGuide>>> getGuidesForTour(
            @PathVariable Long tourId
    ) {
        Tour tour = tourLoader.requireById(tourId);
        List<TourGuide> guides = guideService.getGuidesForTour(tour);
        return ResponseEntity.ok(ApiResponse.ok("Lấy danh sách hướng dẫn viên cho tour thành công", guides));
    }

    private static GuideStatus parseOptionalGuideStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return GuideStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
