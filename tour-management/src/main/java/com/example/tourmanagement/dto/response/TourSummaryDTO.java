package com.example.tourmanagement.dto.response;

import com.example.tourmanagement.model.enums.TourStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class TourSummaryDTO {
    private Long id;
    private String code;
    private String name;
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer durationDays;
    private Integer maxGuests;
    private Integer currentGuests;
    private BigDecimal price;
    private TourStatus status;
    private Integer minGuides;
    private long assignedGuideCount;
}
