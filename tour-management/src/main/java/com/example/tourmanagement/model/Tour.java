package com.example.tourmanagement.model;

import com.example.tourmanagement.model.enums.TourStatus;
import com.example.tourmanagement.model.json.TourJsonViews;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@JsonIgnoreProperties(ignoreUnknown = true, value = {"hibernateLazyInitializer", "handler"})
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(TourJsonViews.ListItem.class)
    private Long id;

    @Column(name = "code", unique = true, nullable = false, length = 20)
    @NotBlank(message = "Mã tour không được để trống")
    @Size(max = 20, message = "Mã tour tối đa 20 ký tự")
    @JsonView(TourJsonViews.ListItem.class)
    private String code;

    @Column(name = "name", nullable = false, length = 200)
    @NotBlank(message = "Tên tour không được để trống")
    @Size(max = 200, message = "Tên tour tối đa 200 ký tự")
    @JsonView(TourJsonViews.ListItem.class)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    @JsonView(TourJsonViews.ListItem.class)
    private String description;

    @Column(name = "destination", nullable = false, length = 200)
    @NotBlank(message = "Điểm đến không được để trống")
    @Size(max = 200, message = "Điểm đến tối đa 200 ký tự")
    @JsonView(TourJsonViews.ListItem.class)
    private String destination;

    @Column(name = "start_date", nullable = false)
    @NotNull(message = "Ngày bắt đầu không được để trống")
    @JsonView(TourJsonViews.ListItem.class)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    @NotNull(message = "Ngày kết thúc không được để trống")
    @JsonView(TourJsonViews.ListItem.class)
    private LocalDate endDate;

    @Column(name = "duration_days")
    @JsonView(TourJsonViews.ListItem.class)
    private Integer durationDays;

    @Column(name = "max_guests")
    @NotNull(message = "Số khách tối đa không được để trống")
    @Min(value = 1, message = "Số khách tối đa phải >= 1")
    @JsonView(TourJsonViews.ListItem.class)
    private Integer maxGuests;

    @Column(name = "current_guests")
    @JsonView(TourJsonViews.ListItem.class)
    private Integer currentGuests = 0;

    @Column(name = "price", precision = 15, scale = 2)
    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0", message = "Giá phải >= 0")
    @JsonView(TourJsonViews.ListItem.class)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @JsonView(TourJsonViews.ListItem.class)
    private TourStatus status = TourStatus.PLANNING;

    // Yêu cầu tour: ngôn ngữ hướng dẫn viên cần biết (vd: "English,French")
    @Column(name = "required_languages", length = 200)
    @JsonView(TourJsonViews.ListItem.class)
    private String requiredLanguages;

    // Yêu cầu chuyên môn (vd: "Mountain,Eco-tourism")
    @Column(name = "required_specialization", length = 200)
    @JsonView(TourJsonViews.ListItem.class)
    private String requiredSpecialization;

    // Số lượng hướng dẫn viên tối thiểu cần thiết
    @Column(name = "min_guides")
    @Min(value = 1, message = "Số HDV tối thiểu phải >= 1")
    @JsonView(TourJsonViews.ListItem.class)
    private Integer minGuides = 1;

    // Khu vực / điểm xuất phát
    @Column(name = "departure_region", length = 100)
    @JsonView(TourJsonViews.ListItem.class)
    private String departureRegion;

    /** Đếm phân công đang hiệu lực — set trong service, không map DB. */
    @Transient
    @JsonView(TourJsonViews.ListItem.class)
    private Long assignedGuideCount;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonView(TourJsonViews.Detail.class)
    private List<TourAssignment> assignments = new ArrayList<>();

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<TourItinerary> itineraries = new ArrayList<>();
}
