package com.example.tourmanagement.model.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueReport {

    private RevenueSummary summary;
    private List<TourRevenue> tours;
}
