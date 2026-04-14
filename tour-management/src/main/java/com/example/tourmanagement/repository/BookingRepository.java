package com.example.tourmanagement.repository;

import com.example.tourmanagement.model.Booking;
import com.example.tourmanagement.model.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByTourId(Long tourId);

    /**
     * Tổng hợp doanh thu và số khách theo tour.
     * Chỉ tính các booking có status trong danh sách truyền vào (CONFIRMED, COMPLETED).
     */
    @Query("""
            SELECT b.tour.id   AS tourId,
                   SUM(b.numberOfGuests) AS totalGuests,
                   SUM(b.totalAmount)    AS totalRevenue
            FROM Booking b
            WHERE b.status IN :statuses
            GROUP BY b.tour.id
            """)
    List<TourRevenueProjection> aggregateRevenueByTour(@Param("statuses") Collection<BookingStatus> statuses);
}
