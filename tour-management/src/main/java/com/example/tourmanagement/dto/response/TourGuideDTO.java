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
    // Trường bổ sung cho chức năng phân công – chỉ được điền bởi getGuidesForTour()
    private String availabilityWarning; // null = phù hợp; else = lý do không phù hợp
    private boolean eligible;           // true nếu availabilityWarning == null
}
