package com.example.tourmanagement.dto.response;

import com.example.tourmanagement.model.enums.TourStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class TourDetailDTO {
    private Long id;
    private String code;
    private String name;
    private String description;
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer durationDays;
    private Integer maxGuests;
    private Integer currentGuests;
    private BigDecimal price;
    private TourStatus status;
    private String requiredLanguages;
    private String requiredSpecialization;
    private Integer minGuides;
    private String departureRegion;
    private List<AssignmentDTO> assignments;
}
