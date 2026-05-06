package com.example.tourmanagement.controller;

import com.example.tourmanagement.controller.support.TourEntityLoader;
import com.example.tourmanagement.dto.response.ApiResponse;
import com.example.tourmanagement.exception.BusinessException;
import com.example.tourmanagement.exception.ResourceNotFoundException;
import com.example.tourmanagement.model.Tour;
import com.example.tourmanagement.model.TourAssignment;
import com.example.tourmanagement.model.TourGuide;
import com.example.tourmanagement.repository.TourAssignmentRepository;
import com.example.tourmanagement.repository.TourGuideRepository;
import com.example.tourmanagement.service.TourAssignmentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
@Validated
public class TourAssignmentController {

    private final TourAssignmentService assignmentService;
    private final TourEntityLoader tourLoader;
    private final TourGuideRepository guideRepository;
    private final TourAssignmentRepository assignmentRepository;

    @GetMapping("/tour/{tourId}")
    public ResponseEntity<ApiResponse<List<TourAssignment>>> getAssignmentsByTour(
            @PathVariable Long tourId
    ) {
        Tour tour = tourLoader.requireById(tourId);
        List<TourAssignment> assignments = assignmentService.getAssignmentsByTour(tour);
        return ResponseEntity.ok(ApiResponse.ok("Lấy danh sách phân công thành công", assignments));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<List<TourAssignment>>> saveAssignments(
            @RequestBody @NotEmpty(message = "Phải chọn ít nhất một hướng dẫn viên")
            List<@Valid TourAssignment> body
    ) {
        TourAssignment first = body.get(0);
        if (first.getTour() == null || first.getTour().getId() == null) {
            throw new BusinessException("Mỗi phân công cần tour có ID");
        }
        Long tourId = first.getTour().getId();
        Tour tour = tourLoader.requireById(tourId);

        List<TourAssignment> pending = new ArrayList<>();
        for (TourAssignment row : body) {
            if (row.getTour() == null || row.getTour().getId() == null
                    || !row.getTour().getId().equals(tourId)) {
                throw new BusinessException("Tất cả phân công phải cùng một tour");
            }
            if (row.getGuide() == null || row.getGuide().getId() == null) {
                throw new BusinessException("Thiếu hướng dẫn viên trong phân công");
            }
            TourGuide guide = guideRepository.findById(row.getGuide().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Hướng dẫn viên", row.getGuide().getId()));
            pending.add(TourAssignment.builder()
                    .tour(tour)
                    .guide(guide)
                    .role(row.getRole() != null ? row.getRole() : "LEAD")
                    .note(row.getNote())
                    .build());
        }

        List<TourAssignment> result = assignmentService.saveAssignments(pending);
        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Phân công hướng dẫn viên thành công! Đã phân công " + result.size() + " hướng dẫn viên.",
                        result
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelAssignment(@PathVariable Long id) {
        TourAssignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Phân công", id));
        assignmentService.cancelAssignment(assignment);
        return ResponseEntity.ok(ApiResponse.ok("Hủy phân công thành công", null));
    }
}
