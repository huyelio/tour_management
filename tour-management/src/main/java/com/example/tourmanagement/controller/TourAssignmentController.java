package com.example.tourmanagement.controller;

import com.example.tourmanagement.dto.request.AssignmentRequestDTO;
import com.example.tourmanagement.dto.response.ApiResponse;
import com.example.tourmanagement.dto.response.AssignmentDTO;
import com.example.tourmanagement.service.TourAssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class TourAssignmentController {

    private final TourAssignmentService assignmentService;

    // GET /api/assignments/tour/{tourId} - Lấy danh sách phân công của tour
    @GetMapping("/tour/{tourId}")
    public ResponseEntity<ApiResponse<List<AssignmentDTO>>> getAssignmentsByTour(
            @PathVariable Long tourId
    ) {
        List<AssignmentDTO> assignments = assignmentService.getAssignmentsByTourId(tourId);
        return ResponseEntity.ok(ApiResponse.ok("Lấy danh sách phân công thành công", assignments));
    }

    // POST /api/assignments - Lưu phân công (có thể phân công nhiều guide cùng lúc)
    @PostMapping
    public ResponseEntity<ApiResponse<List<AssignmentDTO>>> saveAssignments(
            @Valid @RequestBody AssignmentRequestDTO request
    ) {
        List<AssignmentDTO> result = assignmentService.saveAssignments(request);
        return ResponseEntity.ok(
            ApiResponse.ok("Phân công hướng dẫn viên thành công! Đã phân công " + result.size() + " hướng dẫn viên.", result)
        );
    }

    // DELETE /api/assignments/{id} - Hủy phân công
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelAssignment(@PathVariable Long id) {
        assignmentService.cancelAssignment(id);
        return ResponseEntity.ok(ApiResponse.ok("Hủy phân công thành công", null));
    }
}
