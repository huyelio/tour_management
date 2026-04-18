package com.example.tourmanagement.repository;

import com.example.tourmanagement.model.TourGuide;
import com.example.tourmanagement.model.enums.GuideStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TourGuideRepository extends JpaRepository<TourGuide, Long> {

    Optional<TourGuide> findByCode(String code);

    List<TourGuide> findByStatus(GuideStatus status);

    List<TourGuide> findByStatusAndRegionContainingIgnoreCase(GuideStatus status, String region);

    /**
     * Lọc HDV — tránh dùng chuỗi rỗng {@code ''} trong text block (Java parse nhầm).
     * Chuỗi rỗng: dùng LENGTH(TRIM(:param)) = 0.
     */
    @Query(
        "SELECT g FROM TourGuide g WHERE "
            + "(:status IS NULL OR g.status = :status) AND "
            + "(:specialization IS NULL OR LENGTH(TRIM(:specialization)) = 0 "
            + " OR LOWER(g.specialization) LIKE LOWER(CONCAT('%', :specialization, '%'))) AND "
            + "(:language IS NULL OR LENGTH(TRIM(:language)) = 0 "
            + " OR LOWER(g.languages) LIKE LOWER(CONCAT('%', :language, '%'))) AND "
            + "(:region IS NULL OR LENGTH(TRIM(:region)) = 0 "
            + " OR LOWER(g.region) LIKE LOWER(CONCAT('%', :region, '%'))) "
            + "ORDER BY g.fullName ASC"
    )
    List<TourGuide> findByFilters(
        @Param("status") GuideStatus status,
        @Param("specialization") String specialization,
        @Param("language") String language,
        @Param("region") String region
    );

    @Query(
        "SELECT COUNT(a) FROM TourAssignment a WHERE a.guide.id = :guideId "
            + "AND a.status != com.example.tourmanagement.model.enums.AssignmentStatus.CANCELLED "
            + "AND a.tour.id != :excludeTourId "
            + "AND a.tour.status NOT IN ("
            + "com.example.tourmanagement.model.enums.TourStatus.COMPLETED, "
            + "com.example.tourmanagement.model.enums.TourStatus.CANCELLED) "
            + "AND a.tour.startDate <= :endDate AND a.tour.endDate >= :startDate"
    )
    long countScheduleOverlaps(
        @Param("guideId") Long guideId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("excludeTourId") Long excludeTourId
    );

    /**
     * Trả về danh sách ID hướng dẫn viên đang có phân công trùng lịch
     * với khoảng [startDate, endDate] của một tour (loại trừ chính tour đó).
     * Dùng để pre-compute cảnh báo trùng lịch khi load danh sách HDV.
     */
    @Query(
        "SELECT a.guide.id FROM TourAssignment a WHERE "
            + "a.status != com.example.tourmanagement.model.enums.AssignmentStatus.CANCELLED AND "
            + "a.tour.id != :excludeTourId AND "
            + "a.tour.status NOT IN ("
            + "com.example.tourmanagement.model.enums.TourStatus.COMPLETED, "
            + "com.example.tourmanagement.model.enums.TourStatus.CANCELLED) AND "
            + "a.tour.startDate <= :endDate AND a.tour.endDate >= :startDate"
    )
    List<Long> findGuideIdsWithScheduleOverlap(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("excludeTourId") Long excludeTourId
    );
}
