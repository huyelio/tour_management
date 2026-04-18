package com.example.tourmanagement.repository;

import com.example.tourmanagement.model.TourItinerary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourItineraryRepository extends JpaRepository<TourItinerary, Long> {

    @Query("""
            SELECT ti
            FROM TourItinerary ti
            WHERE ti.tour.id = :tourId
            ORDER BY ti.dayNumber ASC, ti.sequenceOrder ASC
            """)
    List<TourItinerary> findByTourIdSorted(@Param("tourId") Long tourId);

    boolean existsByTourId(Long tourId);
}
