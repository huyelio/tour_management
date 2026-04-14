package com.example.tourmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourRevenueDTO {

    private Long tourId;
    private String tourCode;
    private String tourName;
    private String destination;
    private LocalDate startDate;

    /** Tổng số lượt khách từ booking CONFIRMED + COMPLETED */
    private Long totalGuests;

    /** Tổng doanh thu = SUM(totalAmount) từ booking CONFIRMED + COMPLETED */
    private BigDecimal totalRevenue;
}
