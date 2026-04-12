package com.example.tourmanagement.service.impl;

import com.example.tourmanagement.dto.request.AssignmentRequestDTO;
import com.example.tourmanagement.dto.response.AssignmentDTO;
import com.example.tourmanagement.exception.BusinessException;
import com.example.tourmanagement.exception.ResourceNotFoundException;
import com.example.tourmanagement.model.Tour;
import com.example.tourmanagement.model.TourAssignment;
import com.example.tourmanagement.model.TourGuide;
import com.example.tourmanagement.model.enums.AssignmentStatus;
import com.example.tourmanagement.model.enums.GuideStatus;
import com.example.tourmanagement.model.enums.TourStatus;
import com.example.tourmanagement.repository.TourAssignmentRepository;
import com.example.tourmanagement.repository.TourGuideRepository;
import com.example.tourmanagement.repository.TourRepository;
import com.example.tourmanagement.service.TourAssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TourAssignmentServiceImpl implements TourAssignmentService {

    private final TourAssignmentRepository assignmentRepository;
    private final TourRepository tourRepository;
    private final TourGuideRepository guideRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentDTO> getAssignmentsByTourId(Long tourId) {
        return assignmentRepository.findByTourIdWithDetails(tourId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Luồng xử lý "Lưu phân công":
     * 1. Tìm tour theo tourId → kiểm tra tour tồn tại
     * 2. Kiểm tra trạng thái tour (không phân công nếu đã hủy/hoàn thành)
     * 3. Với mỗi hướng dẫn viên trong danh sách:
     *    a. Tìm guide theo guideId → kiểm tra tồn tại
     *    b. Kiểm tra guide đang AVAILABLE
     *    c. Kiểm tra guide không bị trùng lịch với tour này
     *    d. Kiểm tra guide chưa được phân công cho tour này
     * 4. Lưu toàn bộ phân công vào DB trong 1 transaction
     * 5. Cập nhật trạng thái tour nếu cần (đủ số lượng guide)
     */
    @Override
    @Transactional
    public List<AssignmentDTO> saveAssignments(AssignmentRequestDTO request) {
        // Bước 1: Tìm và validate tour
        Tour tour = tourRepository.findById(request.getTourId())
                .orElseThrow(() -> new ResourceNotFoundException("Tour", request.getTourId()));

        // Bước 2: Kiểm tra trạng thái tour
        if (tour.getStatus() == TourStatus.COMPLETED || tour.getStatus() == TourStatus.CANCELLED) {
            throw new BusinessException(
                "Không thể phân công hướng dẫn viên cho tour đã " +
                (tour.getStatus() == TourStatus.COMPLETED ? "hoàn thành" : "hủy")
            );
        }

        List<TourAssignment> savedAssignments = new ArrayList<>();

        // Bước 3: Validate và tạo phân công cho từng hướng dẫn viên
        for (AssignmentRequestDTO.GuideAssignmentItem item : request.getGuides()) {
            // 3a. Tìm hướng dẫn viên
            TourGuide guide = guideRepository.findById(item.getGuideId())
                    .orElseThrow(() -> new ResourceNotFoundException("Hướng dẫn viên", item.getGuideId()));

            // 3b. Kiểm tra trạng thái hoạt động
            if (guide.getStatus() == GuideStatus.INACTIVE) {
                throw new BusinessException(
                    "Hướng dẫn viên '" + guide.getFullName() + "' hiện không hoạt động, không thể phân công"
                );
            }
            if (guide.getStatus() == GuideStatus.ON_LEAVE) {
                throw new BusinessException(
                    "Hướng dẫn viên '" + guide.getFullName() + "' đang nghỉ phép, không thể phân công"
                );
            }

            // 3c. Kiểm tra trùng lịch
            long overlapCount = guideRepository.countScheduleOverlaps(
                guide.getId(),
                tour.getStartDate(),
                tour.getEndDate(),
                tour.getId()
            );
            if (overlapCount > 0) {
                throw new BusinessException(
                    "Hướng dẫn viên '" + guide.getFullName() + "' đã có lịch trùng với tour khác trong khoảng thời gian " +
                    tour.getStartDate() + " đến " + tour.getEndDate()
                );
            }

            // 3d. Kiểm tra đã phân công chưa
            boolean alreadyAssigned = assignmentRepository.existsByTourIdAndGuideIdAndStatusNot(
                tour.getId(), guide.getId(), AssignmentStatus.CANCELLED
            );
            if (alreadyAssigned) {
                throw new BusinessException(
                    "Hướng dẫn viên '" + guide.getFullName() + "' đã được phân công cho tour này rồi"
                );
            }

            // 3e. Tạo phân công
            TourAssignment assignment = TourAssignment.builder()
                    .tour(tour)
                    .guide(guide)
                    .role(item.getRole() != null ? item.getRole() : "LEAD")
                    .note(item.getNote())
                    .status(AssignmentStatus.ASSIGNED)
                    .assignedBy("admin") // TODO: lấy từ security context sau khi thêm auth
                    .build();

            savedAssignments.add(assignmentRepository.save(assignment));
            log.info("Đã phân công guide '{}' cho tour '{}'", guide.getFullName(), tour.getName());
        }

        // Bước 5: Kiểm tra và cập nhật trạng thái tour
        updateTourStatusIfNeeded(tour);

        return savedAssignments.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelAssignment(Long assignmentId) {
        TourAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Phân công", assignmentId));

        assignment.setStatus(AssignmentStatus.CANCELLED);
        assignmentRepository.save(assignment);
        log.info("Đã hủy phân công ID: {}", assignmentId);
    }

    /**
     * Cập nhật trạng thái tour nếu số lượng hướng dẫn viên đã đủ
     */
    private void updateTourStatusIfNeeded(Tour tour) {
        long currentCount = assignmentRepository.countActiveAssignmentsByTourId(tour.getId());
        if (tour.getMinGuides() != null && currentCount >= tour.getMinGuides()
                && tour.getStatus() == TourStatus.PLANNING) {
            tour.setStatus(TourStatus.OPEN);
            tourRepository.save(tour);
            log.info("Tour '{}' đã đủ hướng dẫn viên, cập nhật trạng thái → OPEN", tour.getName());
        }
    }

    private AssignmentDTO toDTO(TourAssignment a) {
        return AssignmentDTO.builder()
                .id(a.getId())
                .tourId(a.getTour().getId())
                .tourCode(a.getTour().getCode())
                .tourName(a.getTour().getName())
                .guideId(a.getGuide().getId())
                .guideCode(a.getGuide().getCode())
                .guideName(a.getGuide().getFullName())
                .guidePhone(a.getGuide().getPhone())
                .guideLanguages(a.getGuide().getLanguages())
                .role(a.getRole())
                .note(a.getNote())
                .status(a.getStatus())
                .assignedAt(a.getAssignedAt())
                .assignedBy(a.getAssignedBy())
                .build();
    }
}
