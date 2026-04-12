package com.example.tourmanagement.repository;

import com.example.tourmanagement.model.Tour;
import com.example.tourmanagement.model.enums.TourStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {

    Optional<Tour> findByCode(String code);

    List<Tour> findByStatus(TourStatus status);

    List<Tour> findByDestinationContainingIgnoreCase(String destination);

    @Query("SELECT t FROM Tour t LEFT JOIN FETCH t.assignments a LEFT JOIN FETCH a.guide WHERE t.id = :id")
    Optional<Tour> findByIdWithAssignments(Long id);

    @Query(
        "SELECT t FROM Tour t WHERE t.status NOT IN ("
            + "com.example.tourmanagement.model.enums.TourStatus.COMPLETED, "
            + "com.example.tourmanagement.model.enums.TourStatus.CANCELLED) "
            + "ORDER BY t.startDate ASC"
    )
    List<Tour> findActiveTours();
}
