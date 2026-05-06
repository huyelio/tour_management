package com.example.tourmanagement.controller.support;

import com.example.tourmanagement.exception.BusinessException;
import com.example.tourmanagement.exception.ResourceNotFoundException;
import com.example.tourmanagement.model.Tour;
import com.example.tourmanagement.repository.TourRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Tải {@link Tour} theo ID cho tầng web — tránh lặp {@code findById().orElseThrow(...)} trong controller.
 * Lỗi thiếu/không tồn tại: {@link ResourceNotFoundException} / {@link BusinessException} (xử lý bởi {@code GlobalExceptionHandler}).
 */
@Component
@RequiredArgsConstructor
public class TourEntityLoader {

    private final TourRepository tourRepository;

    public Tour requireById(Long tourId) {
        if (tourId == null) {
            throw new BusinessException("ID tour không hợp lệ");
        }
        return tourRepository.findById(tourId)
                .orElseThrow(() -> new ResourceNotFoundException("Tour", tourId));
    }
}
