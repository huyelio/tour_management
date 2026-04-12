# Biểu đồ tuần tự – Use Case: Phân công hướng dẫn viên cho tour

## Tổng quan Use Case

| Thông tin | Chi tiết |
|---|---|
| **Tên Use Case** | Phân công hướng dẫn viên cho tour |
| **Actor** | Quản lý (người dùng) |
| **Mục tiêu** | Gán một hoặc nhiều hướng dẫn viên phù hợp cho một tour du lịch |
| **Điều kiện trước** | Người dùng đã đăng nhập, hệ thống đang hoạt động |
| **Điều kiện sau** | Phân công được lưu vào DB, trạng thái tour cập nhật nếu đủ HDV |

---

## Luồng chính – Chia làm 3 giai đoạn

```
Giai đoạn 1: Hiển thị danh sách tour
Giai đoạn 2: Xem chi tiết tour + mở form phân công + tải danh sách HDV
Giai đoạn 3: Lưu phân công (validate + ghi DB + cập nhật trạng thái)
```

---

## GIAI ĐOẠN 1 – Hiển thị danh sách tour

**1.** Người dùng truy cập trang chủ → hệ thống render `TourListPage`.

**2.** `TourListPage` khởi tạo và render component `TourList`.

**3.** `TourList` gọi hàm `fetchTours()` trong `useEffect` để tải dữ liệu khi component mount.

**4.** `fetchTours()` gọi `tourApi.getAll(activeOnly)` – gửi HTTP GET `/api/tours?activeOnly=false` đến server.

**5.** `TourController` nhận request, gọi `tourService.getAllTours()`.

**6.** `TourServiceImpl.getAllTours()` gọi `tourRepository.findAll()` để lấy toàn bộ tour từ DB.

**7.** `TourRepository` thực thi câu truy vấn `SELECT * FROM tours` trên bảng `tours`.

**8.** Database trả về danh sách entity `Tour` cho `TourRepository`.

**9.** `TourRepository` trả về `List<Tour>` cho `TourServiceImpl`.

**10.** `TourServiceImpl` gọi thêm `assignmentRepository.countActiveAssignmentsByTourId(tour.getId())` cho mỗi tour để lấy số lượng HDV đã phân công.

**11.** `TourAssignmentRepository` truy vấn `SELECT COUNT(*) FROM tour_assignments WHERE tour_id = ? AND status != 'CANCELLED'`.

**12.** DB trả về số lượng phân công đang hoạt động.

**13.** `TourServiceImpl` đóng gói mỗi `Tour` thành `TourSummaryDTO` (gọi `toSummaryDTO()`), trả về `List<TourSummaryDTO>`.

**14.** `TourController` bọc kết quả vào `ApiResponse.ok(...)` và trả về `ResponseEntity<ApiResponse<List<TourSummaryDTO>>>` với HTTP 200.

**15.** `tourApi` nhận phản hồi, trả về `res.data` (mảng `TourSummaryDTO`) cho `TourList`.

**16.** `TourList` cập nhật state `tours`, render danh sách `TourCard` – người dùng thấy lưới tour.

---

## GIAI ĐOẠN 2 – Xem chi tiết tour + Mở form phân công + Tải danh sách HDV

### 2A – Xem chi tiết tour

**17.** Người dùng click vào một `TourCard` → React Router điều hướng đến `/tours/:id`, render `TourDetailPage`.

**18.** `TourDetailPage` gọi `fetchTour()` trong `useEffect`.

**19.** `fetchTour()` gọi `tourApi.getById(id)` – gửi HTTP GET `/api/tours/{id}` đến server.

**20.** `TourController` nhận request, gọi `tourService.getTourById(id)`.

**21.** `TourServiceImpl.getTourById(id)` gọi `tourRepository.findByIdWithAssignments(id)`.

**22.** `TourRepository` thực thi JPQL:
```
SELECT t FROM Tour t LEFT JOIN FETCH t.assignments a LEFT JOIN FETCH a.guide WHERE t.id = :id
```
→ Truy vấn bảng `tours` JOIN `tour_assignments` JOIN `tour_guides` trong một lần.

**23.** DB trả về entity `Tour` kèm toàn bộ `TourAssignment` và `TourGuide` liên quan.

**24.** `TourRepository` trả về `Optional<Tour>` cho `TourServiceImpl`.

**25.** `TourServiceImpl` đóng gói `Tour` → `TourDetailDTO` (gọi `toDetailDTO()`), đóng gói mỗi `TourAssignment` → `AssignmentDTO` (gọi `toAssignmentDTO()`).

**26.** `TourController` bọc kết quả vào `ApiResponse.ok(...)` và trả về HTTP 200.

**27.** `tourApi` nhận phản hồi, trả về `res.data` (`TourDetailDTO`) cho `TourDetailPage`.

**28.** `TourDetailPage` cập nhật state `tour`, render thông tin chi tiết tour và component `AssignmentList` (danh sách HDV đã phân công).

### 2B – Mở form phân công và tải danh sách HDV

**29.** Người dùng click nút **"+ Phân công"** → `TourDetailPage` cập nhật state `showAssignForm = true`.

**30.** `TourDetailPage` render component `AssignmentForm` (modal), truyền `tour` và `existingAssignments` qua props.

**31.** `AssignmentForm` khởi tạo với filter mặc định `{ status: 'AVAILABLE', specialization: '', language: '', region: '' }`.

**32.** `useEffect` trong `AssignmentForm` kích hoạt `fetchGuides()` ngay khi mount.

**33.** `fetchGuides()` gọi `guideApi.getAll({ status: 'AVAILABLE' })` – gửi HTTP GET `/api/guides?status=AVAILABLE` đến server.

**34.** `TourGuideController` nhận request, parse query param `status = "AVAILABLE"`, chuyển thành enum `GuideStatus.AVAILABLE`, gọi `guideService.filterGuides(GuideStatus.AVAILABLE, null, null, null)`.

**35.** `TourGuideServiceImpl.filterGuides()` gọi `guideRepository.findByFilters(status, specialization, language, region)`.

**36.** `TourGuideRepository` thực thi JPQL:
```
SELECT g FROM TourGuide g WHERE g.status = :status ... ORDER BY g.fullName ASC
```
→ Truy vấn bảng `tour_guides` lọc theo trạng thái và các tiêu chí.

**37.** DB trả về danh sách entity `TourGuide` thỏa điều kiện.

**38.** `TourGuideRepository` trả về `List<TourGuide>` cho `TourGuideServiceImpl`.

**39.** `TourGuideServiceImpl` đóng gói mỗi `TourGuide` → `TourGuideDTO` (gọi `toDTO()`), trả về `List<TourGuideDTO>`.

**40.** `TourGuideController` bọc vào `ApiResponse.ok(...)` và trả về HTTP 200.

**41.** `guideApi` nhận phản hồi, trả về `res.data` (mảng `TourGuideDTO`) cho `AssignmentForm`.

**42.** `AssignmentForm` cập nhật state `guides`, render danh sách `GuideCard` – người dùng thấy danh sách HDV sẵn sàng.

**43.** *(Tùy chọn)* Người dùng thay đổi bộ lọc (`GuideFilter`) → `filters` state thay đổi → `useEffect` kích hoạt lại `fetchGuides()` → lặp lại bước 33–42 với bộ lọc mới.

---

## GIAI ĐOẠN 3 – Lưu phân công

### 3A – Phía Client: Chọn HDV và submit

**44.** Người dùng click vào `GuideCard` của HDV phù hợp → `handleSelectGuide(guide)` được gọi.

**45.** `AssignmentForm` thêm guide vào `selectedGuides` state: `{ guideId, guideName, role: 'LEAD', note: '' }`.

**46.** *(Tùy chọn)* Người dùng chỉnh sửa vai trò (`LEAD` / `ASSISTANT`) hoặc ghi chú → `handleRoleChange()` / `handleNoteChange()` cập nhật `selectedGuides`.

**47.** Người dùng click nút **"💾 Lưu phân công"** → `handleSubmit()` được gọi.

**48.** `handleSubmit()` kiểm tra `selectedGuides.length > 0`, nếu rỗng hiện lỗi và dừng.

**49.** `handleSubmit()` đóng gói `AssignmentRequestDTO`:
```json
{
  "tourId": 2,
  "guides": [
    { "guideId": 1, "role": "LEAD", "note": "..." },
    { "guideId": 3, "role": "ASSISTANT", "note": "" }
  ]
}
```

**50.** `handleSubmit()` gọi `assignmentApi.save(request)` – gửi HTTP POST `/api/assignments` với body JSON đến server.

---

### 3B – Phía Server: Validate và lưu (TourAssignmentServiceImpl.saveAssignments)

**51.** `TourAssignmentController` nhận request, validate `@Valid @RequestBody AssignmentRequestDTO`, gọi `assignmentService.saveAssignments(request)`.

**52.** `TourAssignmentServiceImpl.saveAssignments()` gọi `tourRepository.findById(request.getTourId())`.

**53.** `TourRepository` truy vấn `SELECT * FROM tours WHERE id = ?`.

**54.** DB trả về entity `Tour`.

**55.** `TourAssignmentServiceImpl` kiểm tra `tour.getStatus()`:
- Nếu `COMPLETED` hoặc `CANCELLED` → ném `BusinessException` → `GlobalExceptionHandler` bắt → trả HTTP 400.
- Ngược lại → tiếp tục.

**56.** *[Lặp cho mỗi GuideAssignmentItem trong danh sách guides]*

**57.** `TourAssignmentServiceImpl` gọi `guideRepository.findById(item.getGuideId())`.

**58.** `TourGuideRepository` truy vấn `SELECT * FROM tour_guides WHERE id = ?`.

**59.** DB trả về entity `TourGuide`.

**60.** Kiểm tra `guide.getStatus()`:
- Nếu `INACTIVE` → ném `BusinessException` → HTTP 400.
- Nếu `ON_LEAVE` → ném `BusinessException` → HTTP 400.
- Ngược lại → tiếp tục.

**61.** `TourAssignmentServiceImpl` gọi `guideRepository.countScheduleOverlaps(guideId, startDate, endDate, tourId)`.

**62.** `TourGuideRepository` thực thi JPQL kiểm tra trùng lịch:
```sql
SELECT COUNT(a) FROM TourAssignment a
WHERE a.guide.id = :guideId
  AND a.status != CANCELLED
  AND a.tour.id != :excludeTourId
  AND a.tour.status NOT IN (COMPLETED, CANCELLED)
  AND a.tour.startDate <= :endDate
  AND a.tour.endDate >= :startDate
```

**63.** DB trả về số lượng tour bị trùng lịch.

**64.** Nếu `overlapCount > 0` → ném `BusinessException` "trùng lịch" → HTTP 400.

**65.** `TourAssignmentServiceImpl` gọi `assignmentRepository.existsByTourIdAndGuideIdAndStatusNot(tourId, guideId, CANCELLED)`.

**66.** `TourAssignmentRepository` truy vấn `SELECT COUNT(*) > 0 FROM tour_assignments WHERE tour_id = ? AND guide_id = ? AND status != 'CANCELLED'`.

**67.** DB trả về `true` / `false`.

**68.** Nếu `alreadyAssigned = true` → ném `BusinessException` "đã phân công" → HTTP 400.

**69.** `TourAssignmentServiceImpl` tạo entity `TourAssignment`:
```java
TourAssignment.builder()
    .tour(tour).guide(guide)
    .role(item.getRole()).note(item.getNote())
    .status(AssignmentStatus.ASSIGNED)
    .assignedBy("admin")
    .build()
```

**70.** Gọi `assignmentRepository.save(assignment)`.

**71.** `TourAssignmentRepository` thực thi `INSERT INTO tour_assignments (tour_id, guide_id, role, note, status, assigned_at, assigned_by) VALUES (...)`.

**72.** DB lưu bản ghi và trả về entity `TourAssignment` đã có `id` và `assignedAt`.

**73.** `TourAssignmentRepository` trả về entity đã lưu cho `TourAssignmentServiceImpl`.

**74.** *[Kết thúc vòng lặp – lặp lại bước 56–73 cho mỗi guide]*

---

### 3C – Cập nhật trạng thái tour và trả về kết quả

**75.** `TourAssignmentServiceImpl` gọi `updateTourStatusIfNeeded(tour)`.

**76.** Trong `updateTourStatusIfNeeded()`: gọi `assignmentRepository.countActiveAssignmentsByTourId(tour.getId())`.

**77.** `TourAssignmentRepository` truy vấn đếm phân công đang hoạt động cho tour.

**78.** DB trả về số lượng HDV đang hoạt động.

**79.** Nếu `currentCount >= tour.getMinGuides()` và `tour.getStatus() == PLANNING`:
- Gọi `tour.setStatus(TourStatus.OPEN)`.
- Gọi `tourRepository.save(tour)`.
- DB cập nhật `UPDATE tours SET status = 'OPEN' WHERE id = ?`.

**80.** `TourAssignmentServiceImpl` đóng gói `List<TourAssignment>` → `List<AssignmentDTO>` (gọi `toDTO()` cho mỗi phần tử).

**81.** `TourAssignmentController` bọc kết quả vào `ApiResponse.ok("Phân công ... thành công!", result)` và trả về HTTP 200.

---

### 3D – Client nhận kết quả và hiển thị

**82.** `assignmentApi` nhận phản hồi HTTP 200, trả về `res` (object `ApiResponse`) cho `handleSubmit()`.

**83.** `handleSubmit()` lấy `res.message` → cập nhật state `successMsg`.

**84.** `handleSubmit()` gọi `setSelectedGuides([])` để reset lựa chọn.

**85.** `handleSubmit()` gọi `onSuccess()` callback từ `TourDetailPage`.

**86.** `TourDetailPage.onSuccess()` gọi `fetchTour()` để reload chi tiết tour (cập nhật danh sách HDV mới).

**87.** `AssignmentForm` render `SuccessAlert` với thông báo thành công – người dùng thấy kết quả phân công.

---

## Luồng thay thế – Trường hợp lỗi

| Tình huống | Xảy ra tại bước | Kết quả |
|---|---|---|
| Tour không tồn tại | 52–54 | `ResourceNotFoundException` → HTTP 404 → FE hiện "Không tìm thấy Tour" |
| Tour đã hủy / hoàn thành | 55 | `BusinessException` → HTTP 400 → FE hiện "Không thể phân công..." |
| HDV không tồn tại | 57–59 | `ResourceNotFoundException` → HTTP 404 |
| HDV không hoạt động (INACTIVE/ON_LEAVE) | 60 | `BusinessException` → HTTP 400 → FE hiện tên HDV + lý do |
| HDV bị trùng lịch | 61–64 | `BusinessException` → HTTP 400 → FE hiện khoảng thời gian trùng |
| HDV đã được phân công | 65–68 | `BusinessException` → HTTP 400 → FE hiện "đã được phân công rồi" |
| Lỗi hệ thống (DB down, v.v.) | Bất kỳ | `Exception` → `GlobalExceptionHandler` → HTTP 500 → FE hiện "Lỗi hệ thống" |
| Không chọn HDV nào | 48 | Xử lý tại FE, không gọi API → hiện "Vui lòng chọn ít nhất một HDV" |
