package com.example.tourmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueSummaryDTO {

    /** Số tour thỏa điều kiện lọc */
    private int totalTours;

    /** Tổng khách từ tất cả tour trong kết quả */
    private Long totalGuests;

    /** Tổng doanh thu từ tất cả tour trong kết quả */
    private BigDecimal totalRevenue;
}
