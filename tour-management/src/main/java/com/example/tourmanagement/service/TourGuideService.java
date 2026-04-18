package com.example.tourmanagement.service;

import com.example.tourmanagement.dto.response.TourGuideDTO;
import com.example.tourmanagement.model.enums.GuideStatus;

import java.util.List;

public interface TourGuideService {
    List<TourGuideDTO> getAllGuides();
    TourGuideDTO getGuideById(Long id);
    List<TourGuideDTO> filterGuides(GuideStatus status, String specialization, String language, String region);

    /**
     * Lấy danh sách tất cả HDV (không lọc theo status) kèm thông tin phù hợp
     * cho tour: cảnh báo trùng lịch, trạng thái không hoạt động.
     * HDV phù hợp (eligible=true) xếp lên trước.
     */
    List<TourGuideDTO> getGuidesForTour(Long tourId, String specialization, String language, String region);
}
