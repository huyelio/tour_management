package com.example.tourmanagement.model;

import com.example.tourmanagement.model.enums.AssignmentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "tour_assignments",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_tour_guide",
        columnNames = {"tour_id", "guide_id"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TourAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nhận từ JSON khi tạo phân công; không trả ra response (tránh vòng lặp tour ↔ assignments).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    @NotNull(message = "Tour không được để trống")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Tour tour;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guide_id", nullable = false)
    @NotNull(message = "Hướng dẫn viên không được để trống")
    private TourGuide guide;

    // Vai trò: LEAD (hướng dẫn chính), ASSISTANT (hỗ trợ)
    @Column(name = "role", length = 20)
    private String role = "LEAD";

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AssignmentStatus status = AssignmentStatus.ASSIGNED;

    @CreationTimestamp
    @Column(name = "assigned_at", updatable = false)
    private LocalDateTime assignedAt;

    @Column(name = "assigned_by", length = 100)
    private String assignedBy;
}
