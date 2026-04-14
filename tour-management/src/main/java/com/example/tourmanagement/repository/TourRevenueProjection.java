package com.example.tourmanagement.repository;

import java.math.BigDecimal;

/**
 * JPA projection: kết quả tổng hợp doanh thu theo tour từ BookingRepository.
 */
public interface TourRevenueProjection {
    Long getTourId();
    Long getTotalGuests();
    BigDecimal getTotalRevenue();
}
