package com.example.tourmanagement.service.impl;

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

@Slf4j
@Service
@RequiredArgsConstructor
public class TourAssignmentServiceImpl implements TourAssignmentService {

    private final TourAssignmentRepository assignmentRepository;
    private final TourRepository tourRepository;
    private final TourGuideRepository guideRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TourAssignment> getAssignmentsByTour(Tour tour) {
        Long tourId = tour.getId();
        if (tourId == null) {
            throw new BusinessException("Tour cần có ID");
        }
        return assignmentRepository.findByTourIdWithDetails(tourId);
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
    public List<TourAssignment> saveAssignments(List<TourAssignment> assignments) {
        if (assignments == null || assignments.isEmpty()) {
            throw new BusinessException("Phải có ít nhất một phân công hướng dẫn viên");
        }

        Tour tour = assignments.get(0).getTour();
        if (tour == null || tour.getId() == null) {
            throw new BusinessException("Tour không hợp lệ");
        }
        for (TourAssignment pending : assignments) {
            if (pending.getTour() == null || pending.getGuide() == null
                    || !pending.getTour().getId().equals(tour.getId())) {
                throw new BusinessException("Mỗi phân công phải cùng một tour và có hướng dẫn viên");
            }
        }

        // Bước 1–2: Trạng thái tour (managed entity — đồng bộ với DB)
        Tour managedTour = tourRepository.findById(tour.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tour", tour.getId()));
        TourStatus tourStatus = managedTour.getStatus();

        if (tourStatus != TourStatus.PLANNING && tourStatus != TourStatus.OPEN) {
            String statusLabel;
            switch (tourStatus) {
                case FULL:
                    statusLabel = "đã đủ khách";
                    break;
                case ONGOING:
                    statusLabel = "đang diễn ra";
                    break;
                case COMPLETED:
                    statusLabel = "đã hoàn thành";
                    break;
                case CANCELLED:
                    statusLabel = "đã hủy";
                    break;
                default:
                    statusLabel = "không hợp lệ để phân công";
                    break;
            }
            throw new BusinessException(
                "Không thể phân công hướng dẫn viên: tour đang ở trạng thái '" + statusLabel + "'"
            );
        }

        List<TourAssignment> savedAssignments = new ArrayList<>();

        for (TourAssignment pending : assignments) {
            TourGuide guide = pending.getGuide();

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

            long overlapCount = guideRepository.countScheduleOverlaps(
                guide.getId(),
                managedTour.getStartDate(),
                managedTour.getEndDate(),
                managedTour.getId()
            );
            if (overlapCount > 0) {
                throw new BusinessException(
                    "Hướng dẫn viên '" + guide.getFullName() + "' đã có lịch trùng với tour khác trong khoảng thời gian " +
                    managedTour.getStartDate() + " đến " + managedTour.getEndDate()
                );
            }

            boolean alreadyAssigned = assignmentRepository.existsByTourIdAndGuideIdAndStatusNot(
                managedTour.getId(), guide.getId(), AssignmentStatus.CANCELLED
            );
            if (alreadyAssigned) {
                throw new BusinessException(
                    "Hướng dẫn viên '" + guide.getFullName() + "' đã được phân công cho tour này rồi"
                );
            }

            TourAssignment assignment = TourAssignment.builder()
                    .tour(managedTour)
                    .guide(guide)
                    .role(pending.getRole() != null ? pending.getRole() : "LEAD")
                    .note(pending.getNote())
                    .status(AssignmentStatus.ASSIGNED)
                    .assignedBy("admin")
                    .build();

            savedAssignments.add(assignmentRepository.save(assignment));
            log.info("Đã phân công guide '{}' cho tour '{}'", guide.getFullName(), managedTour.getName());
        }

        updateTourStatusIfNeeded(managedTour);

        return savedAssignments;
    }

    @Override
    @Transactional
    public void cancelAssignment(TourAssignment assignment) {
        assignment.setStatus(AssignmentStatus.CANCELLED);
        assignmentRepository.save(assignment);
        log.info("Đã hủy phân công ID: {}", assignment.getId());
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

}
