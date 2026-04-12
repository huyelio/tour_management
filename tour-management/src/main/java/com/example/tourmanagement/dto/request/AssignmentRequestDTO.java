package com.example.tourmanagement.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AssignmentRequestDTO {

    @NotNull(message = "ID tour không được để trống")
    private Long tourId;

    @NotEmpty(message = "Phải chọn ít nhất một hướng dẫn viên")
    private List<GuideAssignmentItem> guides;

    @Data
    public static class GuideAssignmentItem {
        @NotNull(message = "ID hướng dẫn viên không được để trống")
        private Long guideId;

        // LEAD hoặc ASSISTANT
        private String role = "LEAD";

        private String note;
    }
}
