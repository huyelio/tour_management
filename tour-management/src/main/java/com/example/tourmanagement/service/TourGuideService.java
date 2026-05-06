package com.example.tourmanagement.service;

import com.example.tourmanagement.model.Tour;
import com.example.tourmanagement.model.TourGuide;
import com.example.tourmanagement.model.enums.GuideStatus;

import java.util.List;

public interface TourGuideService {
    List<TourGuide> getAllGuides();
    TourGuide getGuideById(Long id);
    List<TourGuide> filterGuides(GuideStatus status, String specialization, String language, String region);

    /**
     * Lấy danh sách tất cả HDV cho tour (không lọc specialization/language/region/status),
     * kèm thông tin phù hợp: cảnh báo trùng lịch, trạng thái không hoạt động.
     * HDV phù hợp (eligible=true) xếp lên trước.
     */
    List<TourGuide> getGuidesForTour(Tour tour);
}
