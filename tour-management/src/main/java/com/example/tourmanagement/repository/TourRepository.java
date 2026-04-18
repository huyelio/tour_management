package com.example.tourmanagement.repository;

import com.example.tourmanagement.model.Tour;
import com.example.tourmanagement.model.enums.TourStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {

    Optional<Tour> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    List<Tour> findByStatus(TourStatus status);

    List<Tour> findByDestinationContainingIgnoreCase(String destination);

    @Query("SELECT t FROM Tour t LEFT JOIN FETCH t.assignments a LEFT JOIN FETCH a.guide WHERE t.id = :id")
    Optional<Tour> findByIdWithAssignments(@Param("id") Long id);

    @Query(
        "SELECT t FROM Tour t WHERE t.status NOT IN ("
            + "com.example.tourmanagement.model.enums.TourStatus.COMPLETED, "
            + "com.example.tourmanagement.model.enums.TourStatus.CANCELLED) "
            + "ORDER BY t.startDate ASC"
    )
    List<Tour> findActiveTours();

    @Query("SELECT t FROM Tour t WHERE " +
           "(:keyword IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(t.code) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(t.destination) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:status IS NULL OR t.status = :status) " +
           "ORDER BY t.startDate ASC")
    List<Tour> searchTours(@Param("keyword") String keyword, @Param("status") TourStatus status);
}
