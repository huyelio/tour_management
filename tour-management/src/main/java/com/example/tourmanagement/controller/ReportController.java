package com.example.tourmanagement.controller;

import com.example.tourmanagement.dto.response.ApiResponse;
import com.example.tourmanagement.model.enums.TourStatus;
import com.example.tourmanagement.model.report.RevenueReport;
import com.example.tourmanagement.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Báo cáo doanh thu. Lỗi nghiệp vụ / validation bean / 404 do {@code GlobalExceptionHandler} xử lý thống nhất.
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/tour-revenue")
    public ResponseEntity<ApiResponse<RevenueReport>> getTourRevenue(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,

            @RequestParam(required = false) TourStatus status,

            @RequestParam(required = false) String destination,

            @RequestParam(defaultValue = "revenue_desc") String sortBy
    ) {
        String sort = sortBy != null && !sortBy.isBlank() ? sortBy.trim() : "revenue_desc";
        RevenueReport report = reportService.getTourRevenueReport(
                fromDate, toDate, status, destination, sort);
        return ResponseEntity.ok(ApiResponse.ok("Lấy báo cáo doanh thu thành công", report));
    }
}
