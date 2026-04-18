package com.example.tourmanagement.dto.request;

import com.example.tourmanagement.model.enums.TourStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TourRequestDTO {

    @NotBlank(message = "Mã tour không được để trống")
    @Size(max = 20, message = "Mã tour tối đa 20 ký tự")
    private String code;

    @NotBlank(message = "Tên tour không được để trống")
    @Size(max = 200, message = "Tên tour tối đa 200 ký tự")
    private String name;

    private String description;

    @NotBlank(message = "Điểm đến không được để trống")
    @Size(max = 200, message = "Điểm đến tối đa 200 ký tự")
    private String destination;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDate endDate;

    @NotNull(message = "Số khách tối đa không được để trống")
    @Min(value = 1, message = "Số khách tối đa phải >= 1")
    private Integer maxGuests;

    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0", message = "Giá phải >= 0")
    private BigDecimal price;

    private TourStatus status;

    @Min(value = 1, message = "Số HDV tối thiểu phải >= 1")
    private Integer minGuides;

    private String requiredLanguages;
    private String requiredSpecialization;
    private String departureRegion;
}
