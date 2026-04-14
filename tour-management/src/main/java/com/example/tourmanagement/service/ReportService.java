package com.example.tourmanagement.service;

import com.example.tourmanagement.dto.response.RevenueReportDTO;
import com.example.tourmanagement.model.enums.TourStatus;

import java.time.LocalDate;

public interface ReportService {

    /**
     * Trả về báo cáo doanh thu tour theo các tiêu chí lọc.
     *
     * @param fromDate    Ngày khởi hành từ (inclusive, nullable)
     * @param toDate      Ngày khởi hành đến (inclusive, nullable)
     * @param tourStatus  Trạng thái tour (nullable = tất cả)
     * @param destination Tên điểm đến (nullable/blank = tất cả, tìm kiếm gần đúng)
     * @param sortBy      Cách sắp xếp: revenue_desc | date_desc | name_asc
     */
    RevenueReportDTO getTourRevenueReport(
            LocalDate fromDate,
            LocalDate toDate,
            TourStatus tourStatus,
            String destination,
            String sortBy
    );
}
