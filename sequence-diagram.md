# Biểu đồ tuần tự – Use Case: Phân công hướng dẫn viên cho tour

## Tổng quan Use Case

| Thông tin           | Chi tiết                                                       |
| ------------------- | -------------------------------------------------------------- |
| **Tên Use Case**    | Phân công hướng dẫn viên cho tour                              |
| **Actor**           | Quản lý (người dùng)                                           |
| **Mục tiêu**        | Gán một hoặc nhiều hướng dẫn viên phù hợp cho một tour du lịch |
| **Điều kiện trước** | Người dùng đã đăng nhập, hệ thống đang hoạt động               |
| **Điều kiện sau**   | Phân công được lưu vào DB, trạng thái tour cập nhật nếu đủ HDV |

---

## Các bước sơ đồ tuần tự

### Giai đoạn 1 – Hiển thị danh sách tour

**1.** Người dùng truy cập trang chủ "Quản lý Tour", hệ thống hiển thị trang `TourListPage`.

**2.** `TourListPage` khởi tạo và render component `TourList`.

**3.** `TourList` gọi hàm `fetchTours()` trong `useEffect` để tải danh sách tour khi component được mount.

**4.** `fetchTours()` gọi `tourApi.getAll(activeOnly)` để gửi HTTP GET `/api/tours?activeOnly=false` lên server.

**5.** `TourController` nhận request và gọi hàm `tourService.getAllTours()`.

**6.** `TourServiceImpl.getAllTours()` gọi `tourRepository.findAll()` để lấy toàn bộ tour từ database.

**7.** `TourRepository` thực thi câu truy vấn lấy tất cả bản ghi trong bảng `tours`.

**8.** Database trả về danh sách entity `Tour` cho `TourRepository`.

**9.** `TourRepository` trả về `List<Tour>` cho `TourServiceImpl`.

**10.** Với mỗi tour, `TourServiceImpl` gọi `assignmentRepository.countActiveAssignmentsByTourId(id)` để đếm số HDV đã được phân công.

**11.** `TourAssignmentRepository` thực thi truy vấn đếm số phân công đang hoạt động (`status != CANCELLED`) theo `tourId`.

**12.** Database trả về số lượng phân công đang hoạt động cho mỗi tour.

**13.** `TourServiceImpl` đóng gói từng `Tour` thành `TourSummaryDTO` (gọi `toSummaryDTO()`), trả về `List<TourSummaryDTO>`.

**14.** `TourController` bọc kết quả vào `ApiResponse.ok(...)` và trả về HTTP 200 cho client.

**15.** `tourApi` nhận phản hồi và trả về mảng `TourSummaryDTO` cho `TourList`.

**16.** `TourList` cập nhật state `tours` và render danh sách `TourCard` – người dùng thấy lưới danh sách tour.

---

### Giai đoạn 2A – Xem chi tiết tour

**17.** Người dùng click vào một `TourCard`, React Router điều hướng đến `/tours/:id` và render `TourDetailPage`.

**18.** `TourDetailPage` gọi `fetchTour()` trong `useEffect` để tải thông tin chi tiết tour.

**19.** `fetchTour()` gọi `tourApi.getById(id)` để gửi HTTP GET `/api/tours/{id}` lên server.

**20.** `TourController` nhận request và gọi hàm `tourService.getTourById(id)`.

**21.** `TourServiceImpl.getTourById(id)` gọi `tourRepository.findByIdWithAssignments(id)`.

**22.** `TourRepository` thực thi JPQL kết hợp `tours LEFT JOIN tour_assignments LEFT JOIN tour_guides` với điều kiện `WHERE t.id = :id` để lấy đầy đủ thông tin tour trong một lần truy vấn.

**23.** Database trả về entity `Tour` kèm toàn bộ `TourAssignment` và `TourGuide` liên quan.

**24.** `TourRepository` trả về `Optional<Tour>` cho `TourServiceImpl`.

**25.** `TourServiceImpl` đóng gói `Tour` thành `TourDetailDTO` (gọi `toDetailDTO()`) và từng `TourAssignment` thành `AssignmentDTO` (gọi `toAssignmentDTO()`).

**26.** `TourController` bọc kết quả vào `ApiResponse.ok(...)` và trả về HTTP 200.

**27.** `tourApi` nhận phản hồi và trả về `TourDetailDTO` cho `TourDetailPage`.

**28.** `TourDetailPage` cập nhật state `tour`, hiển thị thông tin chi tiết tour và component `AssignmentList` (danh sách HDV đã phân công).

---

### Giai đoạn 2B – Mở form phân công và tải danh sách HDV

**29.** Người dùng click nút **"+ Phân công"**. `TourDetailPage` kiểm tra `canAssign = ['PLANNING','OPEN'].includes(tour.status)`:
  - Nếu `canAssign = false` → nút bị disable và hiển thị banner thông báo lý do (ví dụ "Tour đang diễn ra, không thể thay đổi phân công"); luồng dừng tại đây.
  - Nếu `canAssign = true` → cập nhật state `showAssignForm = true`, tiếp tục bước 30.

**30.** `TourDetailPage` render component `AssignmentForm` (modal), truyền `tour` và `existingAssignments` qua props.

**31.** `AssignmentForm` khởi tạo với bộ lọc mặc định `{ specialization: '', language: '', region: '' }` (không có `status` – BE tự xác định tính phù hợp theo ngày tour).

**32.** `useEffect` trong `AssignmentForm` kích hoạt `fetchGuides()` ngay khi component được mount.

**33.** `fetchGuides()` gọi `guideApi.getForTour(tour.id, filters)` để gửi HTTP GET `/api/guides/for-tour/{tourId}` lên server.

**34.** `TourGuideController` nhận request và gọi `guideService.getGuidesForTour(tourId, specialization, language, region)`.

**35.** `TourGuideServiceImpl.getGuidesForTour()` gọi `tourRepository.findById(tourId)` để lấy `startDate` và `endDate` của tour.

**36.** `TourGuideServiceImpl` gọi `guideRepository.findGuideIdsWithScheduleOverlap(startDate, endDate, tourId)` để lấy **tập ID HDV** đang bị trùng lịch (1 query duy nhất thay vì N query).

**37.** `TourGuideRepository` thực thi JPQL đếm tất cả `TourAssignment` có `tour.startDate <= endDate AND tour.endDate >= startDate` (ngoại trừ chính tour này, loại trừ tour COMPLETED/CANCELLED), trả về `List<Long>`.

**38.** `TourGuideServiceImpl` gọi `guideRepository.findByFilters(null, specialization, language, region)` để lấy toàn bộ HDV (không lọc status).

**39.** `TourGuideServiceImpl` map từng `TourGuide` sang `TourGuideDTO`, tính `availabilityWarning`:
  - `INACTIVE` → `"Hướng dẫn viên không hoạt động"`
  - `ON_LEAVE` → `"Hướng dẫn viên đang nghỉ phép"`
  - ID nằm trong tập overlap → `"Trùng lịch với tour khác"`
  - Còn lại → `null` (eligible = true)

  Kết quả được sắp xếp: **eligible=true lên trước**, trong nhóm sắp theo tên.

**40.** `TourGuideController` bọc kết quả vào `ApiResponse.ok(...)` và trả về HTTP 200.

**41.** `guideApi` nhận phản hồi và trả về mảng `TourGuideDTO` cho `AssignmentForm`.

**42.** `AssignmentForm` tách mảng thành `eligibleGuides` và `ineligibleGuides`, render 2 nhóm riêng biệt:
  - **Nhóm 1 "✅ Phù hợp"**: `GuideCard` bình thường, có thể click chọn.
  - **Nhóm 2 "⚠ Không thể phân công"**: `GuideCard` mờ, có badge cảnh báo màu vàng, không thể chọn.

---

### Giai đoạn 3A – Phía client: Chọn HDV và submit

**43.** Người dùng click vào `GuideCard` của HDV muốn phân công, `handleSelectGuide(guide)` được gọi.

**44.** `AssignmentForm` thêm guide vào state `selectedGuides` với vai trò mặc định `LEAD` và ghi chú rỗng.

**45.** Người dùng click nút **"💾 Lưu phân công"**, `handleSubmit()` được gọi.

**46.** `handleSubmit()` kiểm tra `selectedGuides.length > 0` – nếu chưa chọn HDV nào thì hiển thị cảnh báo lỗi và dừng lại, không gửi request.

**47.** `handleSubmit()` đóng gói dữ liệu thành `AssignmentRequestDTO` gồm `tourId` và danh sách `guides` (mỗi phần tử có `guideId`, `role`, `note`).

**48.** `handleSubmit()` gọi `assignmentApi.save(request)` để gửi HTTP POST `/api/assignments` với body JSON lên server.

---

### Giai đoạn 3B – Phía server: Validate và lưu phân công

**49.** `TourAssignmentController` nhận request, validate `@Valid @RequestBody AssignmentRequestDTO`, gọi `assignmentService.saveAssignments(request)`.

**50.** `TourAssignmentServiceImpl.saveAssignments()` gọi `tourRepository.findById(request.getTourId())` để tìm tour theo ID.

**51.** `TourRepository` thực thi truy vấn `SELECT * FROM tours WHERE id = ?`.

**52.** Database trả về entity `Tour` cho `TourAssignmentServiceImpl`.

**53.** `TourAssignmentServiceImpl` kiểm tra `tour.getStatus()` – chỉ cho phép phân công nếu status là `PLANNING` hoặc `OPEN`; các trạng thái `FULL`, `ONGOING`, `COMPLETED`, `CANCELLED` đều ném `BusinessException` với mô tả cụ thể, trả về HTTP 400.

_[Vòng lặp – thực hiện cho từng HDV trong danh sách `guides`]_

**54.** `TourAssignmentServiceImpl` gọi `guideRepository.findById(item.getGuideId())` để tìm HDV theo ID.

**55.** `TourGuideRepository` thực thi truy vấn `SELECT * FROM tour_guides WHERE id = ?`.

**56.** Database trả về entity `TourGuide`.

**57.** `TourAssignmentServiceImpl` kiểm tra `guide.getStatus()` – nếu là `INACTIVE` hoặc `ON_LEAVE` thì ném `BusinessException`, trả về HTTP 400.

**58.** `TourAssignmentServiceImpl` gọi `guideRepository.countScheduleOverlaps(guideId, startDate, endDate, tourId)` để kiểm tra HDV có bị trùng lịch không.

**59.** `TourGuideRepository` thực thi JPQL đếm số tour khác mà HDV này đang có lịch trùng với khoảng thời gian của tour hiện tại.

**60.** Database trả về số lượng tour bị trùng lịch.

**61.** Nếu `overlapCount > 0` thì ném `BusinessException` báo lỗi trùng lịch, trả về HTTP 400.

**62.** `TourAssignmentServiceImpl` gọi `assignmentRepository.existsByTourIdAndGuideIdAndStatusNot(tourId, guideId, CANCELLED)` để kiểm tra HDV đã được phân công vào tour này chưa.

**63.** `TourAssignmentRepository` thực thi truy vấn `SELECT COUNT(*) > 0 FROM tour_assignments WHERE tour_id = ? AND guide_id = ? AND status != 'CANCELLED'`.

**64.** Database trả về `true` hoặc `false`.

**65.** Nếu `alreadyAssigned = true` thì ném `BusinessException` báo HDV đã được phân công rồi, trả về HTTP 400.

**66.** `TourAssignmentServiceImpl` tạo entity `TourAssignment` mới với `role`, `note`, `status = ASSIGNED` và `assignedBy = "admin"`.

**67.** Gọi `assignmentRepository.save(assignment)` để lưu phân công.

**68.** `TourAssignmentRepository` thực thi `INSERT INTO tour_assignments (tour_id, guide_id, role, note, status, assigned_at, assigned_by) VALUES (...)`.

**69.** Database lưu bản ghi và trả về entity `TourAssignment` đã có `id` và `assignedAt`.

**70.** `TourAssignmentRepository` trả về entity đã lưu cho `TourAssignmentServiceImpl`.

_[Kết thúc vòng lặp – lặp lại bước 54–70 cho mỗi HDV trong danh sách]_

---

### Giai đoạn 3C – Cập nhật trạng thái tour

**71.** `TourAssignmentServiceImpl` gọi hàm `updateTourStatusIfNeeded(tour)` để kiểm tra và cập nhật trạng thái tour nếu cần.

**72.** `updateTourStatusIfNeeded()` gọi `assignmentRepository.countActiveAssignmentsByTourId(tour.getId())` để đếm tổng số HDV đang được phân công cho tour.

**73.** `TourAssignmentRepository` thực thi truy vấn đếm số phân công đang hoạt động theo `tourId`.

**74.** Database trả về số lượng HDV đang được phân công cho tour.

**75.** Nếu `currentCount >= tour.getMinGuides()` và trạng thái tour đang là `PLANNING`, thì gọi `tour.setStatus(OPEN)` và `tourRepository.save(tour)` để cập nhật – Database thực thi `UPDATE tours SET status = 'OPEN' WHERE id = ?`.

**76.** `TourAssignmentServiceImpl` đóng gói `List<TourAssignment>` vừa lưu thành `List<AssignmentDTO>` (gọi `toDTO()` cho từng phần tử).

**77.** `TourAssignmentController` bọc kết quả vào `ApiResponse.ok("Phân công thành công!", result)` và trả về HTTP 200.

---

### Giai đoạn 3D – Client nhận kết quả và hiển thị

**78.** `assignmentApi` nhận phản hồi HTTP 200 và trả về object `ApiResponse` cho `handleSubmit()`.

**79.** `handleSubmit()` lấy `res.message` và cập nhật state `successMsg` để hiển thị thông báo thành công.

**80.** `handleSubmit()` gọi `setSelectedGuides([])` để reset danh sách HDV đã chọn về rỗng.

**81.** `handleSubmit()` gọi `onSuccess()` callback từ `TourDetailPage`.

**82.** `TourDetailPage.onSuccess()` gọi lại `fetchTour()` để reload chi tiết tour, cập nhật danh sách HDV mới vừa phân công.

**83.** `AssignmentForm` hiển thị `SuccessAlert` với thông báo thành công – người dùng thấy kết quả phân công.

---

### Giai đoạn 4 – Hủy phân công

**84.** Người dùng đang xem `TourDetailPage`; khu vực **Hướng dẫn viên** render `AssignmentList` với prop `assignments` (lấy từ `tour.assignments`) và `onRefresh={fetchTour}`.

**85.** `AssignmentList` tách danh sách thành `active` (các phân công có `status !== 'CANCELLED'`) và `cancelled` (các phân công đã hủy). Chỉ các dòng **active** hiển thị nút **"Hủy"**.

**86.** Người dùng click **"Hủy"** trên một phân công → `handleCancel(a.id)` được gọi.

**87.** `handleCancel` hiển thị `confirm('Bạn có chắc muốn hủy phân công này?')` – nếu người dùng bấm hủy dialog thì luồng dừng, không gọi API.

**88.** Nếu xác nhận, `AssignmentList` gọi `setCancellingId(id)` để disable nút và hiển thị trạng thái chờ.

**89.** `handleCancel` gọi `assignmentApi.cancel(id)` → gửi HTTP **DELETE** `/api/assignments/{id}` lên server (axios base URL đã gồm tiền tố `/api`).

**90.** `TourAssignmentController` nhận request tại `@DeleteMapping("/{id}")`, gọi `assignmentService.cancelAssignment(id)`.

**91.** `TourAssignmentServiceImpl.cancelAssignment(assignmentId)` gọi `assignmentRepository.findById(assignmentId)`.

**92.** `TourAssignmentRepository` thực thi truy vấn lấy bản ghi `tour_assignments` theo khóa chính. Nếu không tồn tại → ném `ResourceNotFoundException` → HTTP **404**.

**93.** Nếu tìm thấy, service gán `assignment.setStatus(AssignmentStatus.CANCELLED)` (hủy mềm: **không xóa** dòng, chỉ đổi trạng thái).

**94.** Gọi `assignmentRepository.save(assignment)` → Database thực thi **UPDATE** `tour_assignments SET status = 'CANCELLED', ...` (các cột khác giữ nguyên theo mapping JPA).

**95.** `TourAssignmentController` trả về HTTP **200** với `ApiResponse.ok("Hủy phân công thành công", null)`.

**96.** `assignmentApi` nhận phản hồi thành công → `handleCancel` gọi `onRefresh()`, tức `TourDetailPage.fetchTour()`.

**97.** `fetchTour()` gọi lại `tourApi.getById(id)` (**GET** `/api/tours/{id}`) để tải lại chi tiết tour kèm danh sách phân công mới nhất.

**98.** `TourDetailPage` cập nhật state `tour`; `AssignmentList` render lại: phân công vừa hủy **không còn** trong nhóm active, có thể xuất hiện trong khối **"Xem … phân công đã hủy"** (nếu có).

**99.** `handleCancel` vào nhánh `finally`, gọi `setCancellingId(null)` để bật lại nút **"Hủy"** cho các dòng khác.

**Ghi chú:** Luồng hủy phân công hiện tại **không** gọi lại `updateTourStatusIfNeeded`: nếu tour đã được chuyển sang `OPEN` vì đủ `minGuides`, sau khi hủy bớt HDV trạng thái tour **vẫn có thể là `OPEN`** cho đến khi có logic nghiệp vụ bổ sung (rollback trạng thái).

---

## Luồng thay thế – Trường hợp lỗi

| Tình huống                              | Xảy ra tại bước | Kết quả                                                                    |
| --------------------------------------- | --------------- | -------------------------------------------------------------------------- |
| Tour không tồn tại                      | 50–52           | `ResourceNotFoundException` → HTTP 404 → FE hiện "Không tìm thấy Tour"     |
| Tour không ở trạng thái PLANNING/OPEN    | 29 (FE), 53 (BE) | FE: disable nút + banner lý do; BE: `BusinessException` → HTTP 400        |
| HDV không tồn tại                       | 54–56           | `ResourceNotFoundException` → HTTP 404                                     |
| HDV không hoạt động (INACTIVE/ON_LEAVE) | 57              | `BusinessException` → HTTP 400 → FE hiện tên HDV + lý do                   |
| HDV bị trùng lịch                       | 58–61           | `BusinessException` → HTTP 400 → FE hiện khoảng thời gian trùng            |
| HDV đã được phân công                   | 62–65           | `BusinessException` → HTTP 400 → FE hiện "đã được phân công rồi"           |
| Lỗi hệ thống (DB down, v.v.)            | Bất kỳ          | `Exception` → `GlobalExceptionHandler` → HTTP 500 → FE hiện "Lỗi hệ thống" |
| Không chọn HDV nào                      | 46              | Xử lý tại FE, không gọi API → hiện "Vui lòng chọn ít nhất một HDV"         |
| Phân công không tồn tại (ID sai)        | 91–92           | `ResourceNotFoundException` → HTTP 404 → FE `alert('Lỗi: ' + err.message)` |
| Lỗi mạng / server khi hủy phân công     | 89–95           | `catch` trong `handleCancel` → `alert` lỗi; `finally` reset `cancellingId` |
| Người dùng hủy dialog xác nhận          | 87              | Không gọi API, không đổi dữ liệu                                           |
