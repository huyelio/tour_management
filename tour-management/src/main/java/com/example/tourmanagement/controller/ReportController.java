package com.example.tourmanagement.controller;

import com.example.tourmanagement.dto.response.ApiResponse;
import com.example.tourmanagement.dto.response.RevenueReportDTO;
import com.example.tourmanagement.model.enums.TourStatus;
import com.example.tourmanagement.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * GET /api/reports/tour-revenue
     *
     * Query params (tất cả đều optional):
     *   fromDate    – yyyy-MM-dd, lọc ngày khởi hành từ
     *   toDate      – yyyy-MM-dd, lọc ngày khởi hành đến
     *   status      – TourStatus enum (PLANNING, OPEN, COMPLETED, ...)
     *   destination – tìm gần đúng theo điểm đến
     *   sortBy      – revenue_desc (default) | date_desc | name_asc
     */
    @GetMapping("/tour-revenue")
    public ResponseEntity<ApiResponse<RevenueReportDTO>> getTourRevenue(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,

            @RequestParam(required = false) TourStatus status,

            @RequestParam(required = false) String destination,

            @RequestParam(defaultValue = "revenue_desc") String sortBy
    ) {
        RevenueReportDTO report = reportService.getTourRevenueReport(
                fromDate, toDate, status, destination, sortBy);
        return ResponseEntity.ok(ApiResponse.ok("Lấy báo cáo doanh thu thành công", report));
    }
}
