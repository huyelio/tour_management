package com.example.tourmanagement.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalTime;

@Data
public class TourItineraryRequestDTO {

    @NotNull(message = "Số ngày không được để trống")
    @Min(value = 1, message = "Số ngày phải >= 1")
    private Integer dayNumber;

    @NotNull(message = "Thứ tự không được để trống")
    @Min(value = 1, message = "Thứ tự phải >= 1")
    private Integer sequenceOrder;

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 200, message = "Tiêu đề tối đa 200 ký tự")
    private String title;

    private String description;
    private String location;
    private LocalTime startTime;
    private LocalTime endTime;
    private String activityType;
    private String note;
    private Boolean isOptional;
}
