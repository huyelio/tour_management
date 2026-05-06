package com.example.tourmanagement.model.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Một dòng doanh thu theo tour (kết quả báo cáo — không phải JPA entity).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourRevenue {

    private Long tourId;
    private String tourCode;
    private String tourName;
    private String destination;
    private LocalDate startDate;
    private Long totalGuests;
    private BigDecimal totalRevenue;
}
