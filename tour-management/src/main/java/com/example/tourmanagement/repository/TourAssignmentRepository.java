package com.example.tourmanagement.repository;

import com.example.tourmanagement.model.TourAssignment;
import com.example.tourmanagement.model.enums.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TourAssignmentRepository extends JpaRepository<TourAssignment, Long> {

    List<TourAssignment> findByTourId(Long tourId);

    List<TourAssignment> findByGuideId(Long guideId);

    Optional<TourAssignment> findByTourIdAndGuideId(Long tourId, Long guideId);

    boolean existsByTourIdAndGuideIdAndStatusNot(Long tourId, Long guideId, AssignmentStatus status);

    @Query(
        "SELECT COUNT(a) FROM TourAssignment a WHERE a.tour.id = :tourId "
            + "AND a.status != com.example.tourmanagement.model.enums.AssignmentStatus.CANCELLED"
    )
    long countActiveAssignmentsByTourId(@Param("tourId") Long tourId);

    @Query(
        "SELECT a FROM TourAssignment a "
            + "JOIN FETCH a.tour t "
            + "JOIN FETCH a.guide g "
            + "WHERE a.tour.id = :tourId "
            + "ORDER BY a.assignedAt DESC"
    )
    List<TourAssignment> findByTourIdWithDetails(@Param("tourId") Long tourId);
}
