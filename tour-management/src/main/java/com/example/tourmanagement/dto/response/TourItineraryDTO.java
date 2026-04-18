package com.example.tourmanagement.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class TourItineraryDTO {
    private Long id;
    private Long tourId;
    private Integer dayNumber;
    private Integer sequenceOrder;
    private String title;
    private String description;
    private String location;
    private LocalTime startTime;
    private LocalTime endTime;
    private String activityType;
    private String note;
    private Boolean isOptional;
}
