package com.example.tourmanagement.dto.response;

import com.example.tourmanagement.model.enums.GuideStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TourGuideDTO {
    private Long id;
    private String code;
    private String fullName;
    private String email;
    private String phone;
    private String specialization;
    private String languages;
    private String region;
    private Integer experienceYears;
    private GuideStatus status;
    private String avatarUrl;
    private String bio;
}
