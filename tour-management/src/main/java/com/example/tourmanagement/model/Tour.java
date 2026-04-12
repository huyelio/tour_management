package com.example.tourmanagement.model;

import com.example.tourmanagement.model.enums.TourStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tours")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true, nullable = false, length = 20)
    private String code;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "destination", nullable = false, length = 200)
    private String destination;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "duration_days")
    private Integer durationDays;

    @Column(name = "max_guests")
    private Integer maxGuests;

    @Column(name = "current_guests")
    private Integer currentGuests = 0;

    @Column(name = "price", precision = 15, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TourStatus status = TourStatus.PLANNING;

    // Yêu cầu tour: ngôn ngữ hướng dẫn viên cần biết (vd: "English,French")
    @Column(name = "required_languages", length = 200)
    private String requiredLanguages;

    // Yêu cầu chuyên môn (vd: "Mountain,Eco-tourism")
    @Column(name = "required_specialization", length = 200)
    private String requiredSpecialization;

    // Số lượng hướng dẫn viên tối thiểu cần thiết
    @Column(name = "min_guides")
    private Integer minGuides = 1;

    // Khu vực / điểm xuất phát
    @Column(name = "departure_region", length = 100)
    private String departureRegion;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TourAssignment> assignments = new ArrayList<>();
}
