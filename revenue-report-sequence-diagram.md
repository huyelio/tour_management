# Biểu đồ tuần tự – Use Case: Thống kê tour theo doanh thu

## Tổng quan Use Case

| Thông tin           | Chi tiết                                                                    |
| ------------------- | --------------------------------------------------------------------------- |
| **Tên Use Case**    | Thống kê tour theo doanh thu                                                |
| **Actor**           | Quản lý (người dùng)                                                        |
| **Mục tiêu**        | Xem báo cáo doanh thu từng tour, lọc theo ngày / trạng thái / điểm đến     |
| **Điều kiện trước** | Người dùng đã đăng nhập, hệ thống đang hoạt động                            |
| **Điều kiện sau**   | Hiển thị bảng thống kê doanh thu, biểu đồ top 5 tour và các số liệu tổng hợp |
| **Ghi chú**         | Chỉ tính booking có trạng thái `CONFIRMED` hoặc `COMPLETED` vào doanh thu   |

---

## Các bước sơ đồ tuần tự

### Giai đoạn 1 – Tải trang và hiển thị báo cáo lần đầu

**1.** Người dùng truy cập trang "Thống kê doanh thu tour" (`/reports/revenue`), React Router render component `RevenueReportPage`.

**2.** `RevenueReportPage` khởi tạo state mặc định:
- `filters = { fromDate: '', toDate: '', status: '', destination: '', sortBy: 'revenue_desc' }`
- `report = null`, `loading = false`, `error = null`

**3.** `useEffect` trong `RevenueReportPage` kích hoạt `fetchReport(filters)` ngay khi component được mount lần đầu.

**4.** `fetchReport()` bật cờ `setLoading(true)` và gọi `reportApi.getTourRevenue(params)`.

**5.** `reportApi.getTourRevenue()` xây dựng query string từ `params` (các trường rỗng bị bỏ qua), gửi HTTP GET `/api/reports/tour-revenue?sortBy=revenue_desc` lên server.

**6.** `ReportController` nhận request, parse các query param (`fromDate=null`, `toDate=null`, `status=null`, `destination=null`, `sortBy="revenue_desc"`) và gọi `reportService.getTourRevenueReport(null, null, null, null, "revenue_desc")`.

**7.** `ReportServiceImpl.getTourRevenueReport()` kiểm tra tính hợp lệ của khoảng ngày – do `fromDate` và `toDate` đều `null` nên bỏ qua bước validate.

**8.** `ReportServiceImpl` gọi `tourRepository.findAll()` để tải toàn bộ danh sách tour từ database.

**9.** `TourRepository` thực thi `SELECT * FROM tours` lấy tất cả bản ghi.

**10.** Database trả về `List<Tour>` cho `TourRepository`, rồi trả về cho `ReportServiceImpl`.

**11.** `ReportServiceImpl` áp dụng bộ lọc trên stream (do tất cả điều kiện lọc đang `null`, toàn bộ tour đều được giữ lại).

**12.** `ReportServiceImpl` gọi `bookingRepository.aggregateRevenueByTour([CONFIRMED, COMPLETED])` để lấy doanh thu tổng hợp theo từng tour.

**13.** `BookingRepository` thực thi câu JPQL:
```sql
SELECT b.tour.id AS tourId,
       SUM(b.numberOfGuests) AS totalGuests,
       SUM(b.totalAmount)    AS totalRevenue
FROM Booking b
WHERE b.status IN ('CONFIRMED', 'COMPLETED')
GROUP BY b.tour.id
```

**14.** Database tổng hợp và trả về `List<TourRevenueProjection>` (mỗi phần tử gồm `tourId`, `totalGuests`, `totalRevenue`) cho `BookingRepository`, rồi trả về `ReportServiceImpl`.

**15.** `ReportServiceImpl` chuyển `List<TourRevenueProjection>` thành `Map<Long, TourRevenueProjection>` với key là `tourId` để tra cứu nhanh.

**16.** `ReportServiceImpl` duyệt qua danh sách tour đã lọc, với mỗi tour:
- Tra cứu `projection` trong `revenueMap` theo `tour.id`
- Đóng gói thành `TourRevenueDTO` gồm: `tourId`, `tourCode`, `tourName`, `destination`, `startDate`, `totalGuests` (0 nếu không có booking), `totalRevenue` (0 nếu không có booking)

**17.** `ReportServiceImpl` sắp xếp `List<TourRevenueDTO>` theo tiêu chí `sortBy = "revenue_desc"` (giảm dần theo doanh thu).

**18.** `ReportServiceImpl` tính tổng toàn bộ danh sách:
- `totalRevenue = sum(tourRevenue.totalRevenue)`
- `totalGuests  = sum(tourRevenue.totalGuests)`
- `totalTours   = tourRevenues.size()`

**19.** `ReportServiceImpl` tạo `RevenueSummaryDTO { totalTours, totalRevenue, totalGuests }` và đóng gói kết quả cuối cùng thành `RevenueReportDTO { summary, tours }`, trả về cho `ReportController`.

**20.** `ReportController` bọc kết quả vào `ApiResponse.ok("Lấy báo cáo doanh thu thành công", report)` và trả về HTTP 200.

**21.** `reportApi` nhận phản hồi, extract `r.data` và trả về `ApiResponse<RevenueReportDTO>` cho `fetchReport()`.

**22.** `fetchReport()` gọi `setReport(res.data)` để lưu kết quả vào state, `setLoading(false)` để tắt spinner.

**23.** `RevenueReportPage` render toàn bộ kết quả cho người dùng:
- **3 thẻ tổng hợp** (`SummaryCard`): Tổng doanh thu, Tổng số khách, Tổng số tour
- **Biểu đồ cột** (`RevenueBarChart`): Hiển thị top 5 tour có doanh thu cao nhất theo SVG thuần
- **Bảng chi tiết**: Danh sách tất cả tour kèm doanh thu, số khách, ngày khởi hành; hàng cuối là tổng cộng

---

### Giai đoạn 2 – Người dùng nhập bộ lọc và tìm kiếm

**24.** Người dùng điền các điều kiện lọc vào form:
- **Từ ngày** (`fromDate`): ví dụ `2026-01-01`
- **Đến ngày** (`toDate`): ví dụ `2026-12-31`
- **Trạng thái tour** (`status`): ví dụ `COMPLETED`
- **Điểm đến** (`destination`): ví dụ `Đà Nẵng`
- **Sắp xếp theo** (`sortBy`): ví dụ `date_desc`

Mỗi thay đổi gọi `setFilters(prev => ({ ...prev, field: value }))` cập nhật state `filters`.

**25.** Người dùng click nút **"🔍 Tìm kiếm"**, sự kiện `onSubmit` kích hoạt `handleSearch(e)`.

**26.** `handleSearch()` gọi `e.preventDefault()` ngăn reload trang, sau đó gọi `fetchReport(filters)`.

**27.** `fetchReport()` gọi `reportApi.getTourRevenue(params)`, lần này query string đầy đủ hơn:
`GET /api/reports/tour-revenue?fromDate=2026-01-01&toDate=2026-12-31&status=COMPLETED&destination=Đà Nẵng&sortBy=date_desc`

**28.** `ReportController` nhận request, parse các param (bao gồm `fromDate`, `toDate` theo định dạng `ISO.DATE`; `status` tự động convert thành `TourStatus` enum) và gọi `reportService.getTourRevenueReport(fromDate, toDate, COMPLETED, "Đà Nẵng", "date_desc")`.

**29.** `ReportServiceImpl` validate: kiểm tra `fromDate.isAfter(toDate)` – nếu hợp lệ thì tiếp tục; nếu không thì ném `BusinessException`.

**30.** `ReportServiceImpl` gọi `tourRepository.findAll()` rồi áp dụng stream filter:
- Chỉ giữ tour có `startDate >= fromDate`
- Chỉ giữ tour có `startDate <= toDate`
- Chỉ giữ tour có `status == COMPLETED`
- Chỉ giữ tour có `destination` chứa chuỗi `"Đà Nẵng"` (không phân biệt hoa thường)

**31.** `ReportServiceImpl` gọi `bookingRepository.aggregateRevenueByTour([CONFIRMED, COMPLETED])` – câu truy vấn vẫn tổng hợp toàn bộ booking hợp lệ trong DB (lọc tour đã làm ở bước 30).

**32.** Các bước gộp dữ liệu, sắp xếp (`date_desc`), tính tổng, đóng gói `RevenueReportDTO` thực hiện tương tự bước 15–19.

**33.** `ReportController` trả về HTTP 200 với `RevenueReportDTO` đã lọc.

**34.** `RevenueReportPage` cập nhật state `report`, re-render: biểu đồ và bảng chỉ hiển thị các tour thỏa điều kiện lọc.

---

### Giai đoạn 3 – Người dùng đặt lại bộ lọc

**35.** Người dùng click nút **"Đặt lại"**, `handleReset()` được gọi.

**36.** `handleReset()` tạo object `defaults = { fromDate:'', toDate:'', status:'', destination:'', sortBy:'revenue_desc' }`.

**37.** `handleReset()` gọi `setFilters(defaults)` để reset form về trạng thái ban đầu.

**38.** `handleReset()` ngay lập tức gọi `fetchReport(defaults)` để tải lại báo cáo không lọc.

**39.** Luồng xử lý giống Giai đoạn 1 (bước 4–23), trang hiển thị toàn bộ tour không có bộ lọc.

---

## Luồng thay thế – Trường hợp lỗi

| Tình huống                            | Xảy ra tại bước | Kết quả                                                                       |
| ------------------------------------- | --------------- | ----------------------------------------------------------------------------- |
| `fromDate` sau `toDate`               | 29              | `BusinessException` → HTTP 400 → FE hiện "Ngày bắt đầu không được sau ngày kết thúc" |
| `status` không hợp lệ (sai enum)     | 28              | Spring parse lỗi → HTTP 400 → FE hiện thông báo lỗi                          |
| Không có tour nào thỏa điều kiện     | 30–34           | `tours = []` → HTTP 200 → FE hiển thị "📭 Không có dữ liệu phù hợp..."       |
| Lỗi kết nối database                 | 9 hoặc 13       | `Exception` → `GlobalExceptionHandler` → HTTP 500 → FE hiện "Lỗi hệ thống"   |
| Mạng bị ngắt khi gọi API             | 5               | `axios` ném lỗi network → `catch` trong `fetchReport` → `setError(err.message)` → FE hiện `ErrorAlert` |
