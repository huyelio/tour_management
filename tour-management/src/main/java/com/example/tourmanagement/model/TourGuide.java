package com.example.tourmanagement.model;

import com.example.tourmanagement.model.enums.GuideStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tour_guides")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourGuide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true, nullable = false, length = 20)
    private String code;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "email", unique = true, length = 100)
    private String email;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    // Chuyên môn: Beach, Mountain, City, Cultural, Eco-tourism, ...
    @Column(name = "specialization", length = 200)
    private String specialization;

    // Ngoại ngữ: English, French, Japanese, Chinese, Korean, ...
    @Column(name = "languages", length = 200)
    private String languages;

    // Khu vực hoạt động: Miền Bắc, Miền Trung, Miền Nam, Toàn quốc
    @Column(name = "region", length = 100)
    private String region;

    @Column(name = "experience_years")
    private Integer experienceYears = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GuideStatus status = GuideStatus.AVAILABLE;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @OneToMany(mappedBy = "guide", cascade = CascadeType.ALL)
    private List<TourAssignment> assignments = new ArrayList<>();
}
