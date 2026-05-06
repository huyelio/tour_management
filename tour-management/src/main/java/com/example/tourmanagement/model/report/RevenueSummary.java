package com.example.tourmanagement.model.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueSummary {

    private int totalTours;
    private Long totalGuests;
    private BigDecimal totalRevenue;
}
