package com.example.tourmanagement.service.impl;

import com.example.tourmanagement.exception.BusinessException;
import com.example.tourmanagement.model.Tour;
import com.example.tourmanagement.model.enums.BookingStatus;
import com.example.tourmanagement.model.enums.TourStatus;
import com.example.tourmanagement.model.report.RevenueReport;
import com.example.tourmanagement.model.report.RevenueSummary;
import com.example.tourmanagement.model.report.TourRevenue;
import com.example.tourmanagement.repository.BookingRepository;
import com.example.tourmanagement.repository.TourRepository;
import com.example.tourmanagement.repository.TourRevenueProjection;
import com.example.tourmanagement.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final TourRepository tourRepository;
    private final BookingRepository bookingRepository;

    // Chỉ tính booking CONFIRMED hoặc COMPLETED vào doanh thu
    private static final List<BookingStatus> REVENUE_STATUSES =
            List.of(BookingStatus.CONFIRMED, BookingStatus.COMPLETED);

    @Override
    public RevenueReport getTourRevenueReport(
            LocalDate fromDate, LocalDate toDate,
            TourStatus tourStatus, String destination, String sortBy) {

        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new BusinessException("Ngày bắt đầu không được sau ngày kết thúc");
        }

        // 1. Lọc tour theo điều kiện
        List<Tour> tours = tourRepository.findAll().stream()
                .filter(t -> fromDate == null || !t.getStartDate().isBefore(fromDate))
                .filter(t -> toDate == null || !t.getStartDate().isAfter(toDate))
                .filter(t -> tourStatus == null || t.getStatus() == tourStatus)
                .filter(t -> destination == null || destination.isBlank()
                        || t.getDestination().toLowerCase().contains(destination.toLowerCase()))
                .collect(Collectors.toList());

        // 2. Tổng hợp doanh thu theo tour_id từ booking CONFIRMED + COMPLETED
        Map<Long, TourRevenueProjection> revenueMap = bookingRepository
                .aggregateRevenueByTour(REVENUE_STATUSES).stream()
                .collect(Collectors.toMap(TourRevenueProjection::getTourId, p -> p));

        // 3. Gộp thông tin tour + doanh thu
        List<TourRevenue> tourRevenues = tours.stream()
                .map(t -> {
                    TourRevenueProjection proj = revenueMap.get(t.getId());
                    return TourRevenue.builder()
                            .tourId(t.getId())
                            .tourCode(t.getCode())
                            .tourName(t.getName())
                            .destination(t.getDestination())
                            .startDate(t.getStartDate())
                            .totalGuests(proj != null ? proj.getTotalGuests() : 0L)
                            .totalRevenue(proj != null && proj.getTotalRevenue() != null
                                    ? proj.getTotalRevenue() : BigDecimal.ZERO)
                            .build();
                })
                .collect(Collectors.toList());

        // 4. Sắp xếp
        sortTourRevenues(tourRevenues, sortBy);

        // 5. Tính tổng
        BigDecimal totalRevenue = tourRevenues.stream()
                .map(TourRevenue::getTotalRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long totalGuests = tourRevenues.stream()
                .mapToLong(TourRevenue::getTotalGuests)
                .sum();

        RevenueSummary summary = RevenueSummary.builder()
                .totalTours(tourRevenues.size())
                .totalRevenue(totalRevenue)
                .totalGuests(totalGuests)
                .build();

        return RevenueReport.builder()
                .summary(summary)
                .tours(tourRevenues)
                .build();
    }

    private void sortTourRevenues(List<TourRevenue> list, String sortBy) {
        String key = sortBy != null ? sortBy : "";
        Comparator<TourRevenue> comparator;
        if ("date_desc".equals(key)) {
            comparator = Comparator.comparing(
                    TourRevenue::getStartDate, Comparator.nullsLast(Comparator.reverseOrder()));
        } else if ("name_asc".equals(key)) {
            comparator = Comparator.comparing(
                    TourRevenue::getTourName, String.CASE_INSENSITIVE_ORDER);
        } else {
            comparator = Comparator.comparing(
                    TourRevenue::getTotalRevenue, Comparator.nullsLast(Comparator.reverseOrder()));
        }
        list.sort(comparator);
    }
}
