package com.example.tourmanagement.dto.response;

import com.example.tourmanagement.model.enums.AssignmentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AssignmentDTO {
    private Long id;
    private Long tourId;
    private String tourCode;
    private String tourName;
    private Long guideId;
    private String guideCode;
    private String guideName;
    private String guidePhone;
    private String guideLanguages;
    private String role;
    private String note;
    private AssignmentStatus status;
    private LocalDateTime assignedAt;
    private String assignedBy;
}
