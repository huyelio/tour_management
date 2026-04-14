# Tour Management System

Hệ thống quản lý tour du lịch và phân công hướng dẫn viên, xây dựng theo kiến trúc **3-tier** với React (Frontend), Spring Boot (Backend) và MySQL (Database), đóng gói hoàn toàn bằng Docker.

---

## Mục lục

- [Tổng quan kiến trúc](#tổng-quan-kiến-trúc)
- [Công nghệ sử dụng](#công-nghệ-sử-dụng)
- [Cấu trúc dự án](#cấu-trúc-dự-án)
- [Mô hình dữ liệu](#mô-hình-dữ-liệu)
- [API Endpoints](#api-endpoints)
- [Hướng dẫn chạy với Docker](#hướng-dẫn-chạy-với-docker)
- [Chạy từng thành phần riêng lẻ](#chạy-từng-thành-phần-riêng-lẻ)
- [Biến môi trường](#biến-môi-trường)
- [Tài liệu bổ sung](#tài-liệu-bổ-sung)

---

## Tổng quan kiến trúc

```
┌─────────────────────────────────────────────────────────┐
│                      Docker Network                      │
│                                                          │
│  ┌──────────────┐    HTTP     ┌──────────────────────┐  │
│  │   Frontend   │ ──────────▶ │      Backend         │  │
│  │ React + Nginx│  :8080/api  │   Spring Boot 3.3.4  │  │
│  │  port: 8081  │             │     port: 8080       │  │
│  └──────────────┘             └──────────┬───────────┘  │
│                                          │ JDBC          │
│                                ┌─────────▼───────────┐  │
│                                │      Database        │  │
│                                │     MySQL 8.0        │  │
│                                │     port: 3307       │  │
│                                └─────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

| Thành phần | URL truy cập              | Mô tả                        |
| ---------- | ------------------------- | ---------------------------- |
| Frontend   | http://localhost:8081     | Giao diện người dùng (React) |
| Backend    | http://localhost:8080     | REST API (Spring Boot)       |
| MySQL      | localhost:3307            | Cơ sở dữ liệu                |

---

## Công nghệ sử dụng

### Backend
| Công nghệ          | Phiên bản | Mục đích                      |
| ------------------ | --------- | ----------------------------- |
| Java               | 21        | Ngôn ngữ lập trình            |
| Spring Boot        | 3.3.4     | Framework REST API            |
| Spring Data JPA    | —         | ORM, truy vấn database        |
| Spring Validation  | —         | Validate dữ liệu đầu vào      |
| MySQL Connector/J  | —         | Driver kết nối MySQL          |
| Lombok             | —         | Giảm boilerplate code         |
| Maven              | —         | Build & dependency management |

### Frontend
| Công nghệ       | Phiên bản | Mục đích                    |
| --------------- | --------- | --------------------------- |
| React           | 19        | UI framework                |
| React Router    | 7         | Điều hướng SPA              |
| Axios           | 1.x       | HTTP client gọi REST API    |
| Vite            | 8.x       | Build tool & dev server     |
| TypeScript      | 6.x       | Type checking               |
| Nginx           | alpine    | Serve static file (prod)    |

### Infrastructure
| Công nghệ      | Mục đích                           |
| -------------- | ---------------------------------- |
| Docker         | Container hóa từng thành phần      |
| Docker Compose | Orchestrate toàn bộ hệ thống       |
| MySQL 8.0      | Relational database                |

---

## Cấu trúc dự án

```
final/
├── docker-compose.yml              # Orchestrate toàn bộ hệ thống
├── .env                            # Biến môi trường (DB credentials, v.v.)
├── init.sql                        # Script khởi tạo database
├── class-diagram.puml              # Sơ đồ lớp (PlantUML)
├── sequence-diagram.md             # Biểu đồ tuần tự (Markdown)
├── sequence-diagram.puml           # Biểu đồ tuần tự (PlantUML)
│
├── tour-management/                # Backend – Spring Boot
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/example/tourmanagement/
│       ├── TourManagementApplication.java
│       ├── config/
│       │   └── CorsConfig.java             # Cấu hình CORS
│       ├── controller/
│       │   ├── TourController.java
│       │   ├── TourGuideController.java
│       │   └── TourAssignmentController.java
│       ├── service/
│       │   ├── TourService.java            # Interface
│       │   ├── TourGuideService.java
│       │   ├── TourAssignmentService.java
│       │   └── impl/                       # Implementations
│       ├── repository/
│       │   ├── TourRepository.java
│       │   ├── TourGuideRepository.java
│       │   └── TourAssignmentRepository.java
│       ├── model/
│       │   ├── Tour.java
│       │   ├── TourGuide.java
│       │   ├── TourAssignment.java
│       │   └── enums/
│       │       ├── TourStatus.java         # PLANNING, OPEN, FULL, ONGOING, COMPLETED, CANCELLED
│       │       ├── GuideStatus.java        # AVAILABLE, ON_TOUR, ON_LEAVE, INACTIVE
│       │       └── AssignmentStatus.java   # ASSIGNED, CONFIRMED, CANCELLED
│       ├── dto/
│       │   ├── request/AssignmentRequestDTO.java
│       │   └── response/                   # TourSummaryDTO, TourDetailDTO, TourGuideDTO, AssignmentDTO, ApiResponse
│       └── exception/
│           ├── BusinessException.java
│           ├── ResourceNotFoundException.java
│           └── GlobalExceptionHandler.java
│
└── FE/tour-management-fe/          # Frontend – React
    ├── Dockerfile
    ├── nginx.conf
    ├── package.json
    └── src/
        ├── App.jsx                         # Router + Layout
        ├── main.jsx
        ├── api/
        │   ├── axiosConfig.js              # Base URL, interceptors
        │   ├── tourApi.js
        │   ├── guideApi.js
        │   └── assignmentApi.js
        ├── pages/
        │   ├── TourListPage.jsx
        │   └── TourDetailPage.jsx
        ├── components/
        │   ├── tour/       (TourList, TourCard)
        │   ├── guide/      (GuideCard, GuideFilter)
        │   ├── assignment/ (AssignmentForm, AssignmentList)
        │   └── common/     (Badge, LoadingSpinner, ErrorAlert, SuccessAlert)
        └── utils/helpers.js
```

---

## Tính năng

| Tính năng | Đường dẫn | Mô tả |
| --------- | --------- | ----- |
| Danh sách tour | `/` | Xem, tìm kiếm, lọc tour |
| Chi tiết tour | `/tours/:id` | Thông tin tour + phân công HDV |
| Thống kê doanh thu | `/reports/revenue` | Lọc, sắp xếp, biểu đồ top 5 |

---

## Mô hình dữ liệu

### Các entity chính

**Tour** — Thông tin tour du lịch
| Trường                 | Kiểu          | Mô tả                                  |
| ---------------------- | ------------- | -------------------------------------- |
| id                     | Long (PK)     | Khóa chính                             |
| code                   | String        | Mã tour (duy nhất)                     |
| name                   | String        | Tên tour                               |
| destination            | String        | Điểm đến                               |
| startDate / endDate    | LocalDate     | Ngày bắt đầu / kết thúc               |
| durationDays           | Integer       | Số ngày                                |
| maxGuests              | Integer       | Số khách tối đa                        |
| price                  | BigDecimal    | Giá tour                               |
| status                 | TourStatus    | Trạng thái tour                        |
| minGuides              | Integer       | Số HDV tối thiểu cần phân công         |
| requiredLanguages      | String        | Ngôn ngữ yêu cầu                       |
| requiredSpecialization | String        | Chuyên môn yêu cầu                     |

**TourGuide** — Thông tin hướng dẫn viên
| Trường          | Kiểu        | Mô tả                      |
| --------------- | ----------- | -------------------------- |
| id              | Long (PK)   | Khóa chính                 |
| code            | String      | Mã HDV (duy nhất)          |
| fullName        | String      | Họ và tên                  |
| specialization  | String      | Chuyên môn                 |
| languages       | String      | Ngôn ngữ thành thạo        |
| region          | String      | Khu vực hoạt động          |
| experienceYears | Integer     | Số năm kinh nghiệm         |
| status          | GuideStatus | Trạng thái HDV             |

**TourAssignment** — Phân công HDV vào tour (bảng trung gian)
| Trường     | Kiểu             | Mô tả                             |
| ---------- | ---------------- | --------------------------------- |
| id         | Long (PK)        | Khóa chính                        |
| tour       | Tour (FK)        | Tour được phân công               |
| guide      | TourGuide (FK)   | HDV được phân công                |
| role       | String           | Vai trò (LEAD / SUPPORT)          |
| note       | String           | Ghi chú                           |
| status     | AssignmentStatus | Trạng thái phân công              |
| assignedAt | LocalDateTime    | Thời điểm phân công               |
| assignedBy | String           | Người thực hiện phân công         |

> **Ràng buộc:** `UNIQUE(tour_id, guide_id)` — mỗi HDV chỉ được phân công một lần vào một tour.

**Customer** — Khách hàng
| Trường    | Kiểu          | Mô tả             |
| --------- | ------------- | ----------------- |
| id        | Long (PK)     | Khóa chính        |
| code      | String        | Mã khách (duy nhất) |
| fullName  | String        | Họ và tên         |
| phone     | String        | Số điện thoại     |
| email     | String        | Email             |
| createdAt | LocalDateTime | Ngày tạo          |

**Booking** — Đặt chỗ / đơn hàng
| Trường          | Kiểu          | Mô tả                                               |
| --------------- | ------------- | --------------------------------------------------- |
| id              | Long (PK)     | Khóa chính                                          |
| bookingCode     | String        | Mã booking (duy nhất)                               |
| tour            | Tour (FK)     | Tour được đặt                                       |
| customer        | Customer (FK) | Khách hàng đặt                                      |
| bookingDate     | LocalDate     | Ngày đặt                                            |
| numberOfGuests  | Integer       | Số lượng khách                                      |
| unitPrice       | BigDecimal    | Giá/khách tại thời điểm đặt                         |
| discountAmount  | BigDecimal    | Số tiền giảm giá (0 nếu không có)                   |
| totalAmount     | BigDecimal    | `numberOfGuests × unitPrice − discountAmount`        |
| status          | BookingStatus | PENDING / CONFIRMED / CANCELLED / COMPLETED         |

> **Quy tắc tính doanh thu:** Chỉ tính booking có `status = CONFIRMED` hoặc `COMPLETED`. Booking `CANCELLED` không được tính.

### Quan hệ
```
Tour (1) ──── (0..*) TourAssignment (0..*) ──── (1) TourGuide
Tour (1) ──── (0..*) Booking        (0..*) ──── (1) Customer
```

---

## API Endpoints

Tất cả response đều bọc trong `ApiResponse<T>`:
```json
{
  "success": true,
  "message": "...",
  "data": { ... },
  "timestamp": "2026-04-14T10:00:00"
}
```

### Tours — `/api/tours`
| Method | Endpoint        | Mô tả                                  |
| ------ | --------------- | -------------------------------------- |
| GET    | `/api/tours`    | Lấy danh sách tour (`?activeOnly=bool`)|
| GET    | `/api/tours/{id}` | Lấy chi tiết tour kèm danh sách HDV  |

### Tour Guides — `/api/guides`
| Method | Endpoint          | Mô tả                                              |
| ------ | ----------------- | -------------------------------------------------- |
| GET    | `/api/guides`     | Lấy danh sách HDV (filter: `status`, `specialization`, `language`, `region`) |
| GET    | `/api/guides/{id}`| Lấy chi tiết HDV                                  |

### Reports — `/api/reports`
| Method | Endpoint                    | Mô tả                                   |
| ------ | --------------------------- | --------------------------------------- |
| GET    | `/api/reports/tour-revenue` | Thống kê doanh thu tour theo bộ lọc     |

**Query params** `/api/reports/tour-revenue`:
| Param       | Kiểu      | Mô tả                                      |
| ----------- | --------- | ------------------------------------------ |
| fromDate    | yyyy-MM-dd | Lọc ngày khởi hành từ (tùy chọn)          |
| toDate      | yyyy-MM-dd | Lọc ngày khởi hành đến (tùy chọn)         |
| status      | TourStatus | Lọc trạng thái tour (tùy chọn)            |
| destination | String     | Tìm gần đúng theo điểm đến (tùy chọn)    |
| sortBy      | String     | `revenue_desc` (mặc định) / `date_desc` / `name_asc` |

### Assignments — `/api/assignments`
| Method | Endpoint                   | Mô tả                              |
| ------ | -------------------------- | ---------------------------------- |
| GET    | `/api/assignments?tourId=` | Lấy danh sách phân công theo tour  |
| POST   | `/api/assignments`         | Tạo mới phân công (batch)          |
| DELETE | `/api/assignments/{id}`    | Hủy phân công                      |

#### Request body POST `/api/assignments`
```json
{
  "tourId": 1,
  "guides": [
    { "guideId": 5, "role": "LEAD", "note": "Hướng dẫn chính" },
    { "guideId": 8, "role": "SUPPORT", "note": "" }
  ]
}
```

### Mã lỗi HTTP
| Code | Tình huống                                           |
| ---- | ---------------------------------------------------- |
| 200  | Thành công                                           |
| 400  | Lỗi nghiệp vụ (tour đã hủy, HDV bận, trùng lịch...) |
| 404  | Không tìm thấy Tour / TourGuide                      |
| 500  | Lỗi hệ thống                                         |

---

## Hướng dẫn chạy với Docker

### Yêu cầu
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (bao gồm Docker Compose)

### Các bước

**1. Clone / tải source code về máy**

**2. Tạo file `.env` từ mẫu**
```bash
# File .env đã có sẵn, kiểm tra và chỉnh sửa nếu cần:
DB_NAME=tour_management_db
DB_USERNAME=root
DB_PASSWORD=TourAdmin@2026
```

**3. Khởi động toàn bộ hệ thống**
```bash
docker compose up --build
```

> Lần đầu build sẽ mất vài phút do tải dependencies (Maven, npm). Các lần sau sẽ nhanh hơn nhờ Docker cache layer.

**4. Truy cập ứng dụng**
- Giao diện web: **http://localhost:8081**
- REST API: **http://localhost:8080/api**
- MySQL: `localhost:3307` (user: `root`, password: theo `.env`)

**5. Dừng hệ thống**
```bash
# Dừng nhưng giữ data
docker compose down

# Dừng và xóa toàn bộ data (volumes)
docker compose down -v
```

### Thứ tự khởi động
Docker Compose đảm bảo thứ tự khởi động:
```
MySQL (healthcheck ready) → Backend (Spring Boot) → Frontend (Nginx)
```

---

## Chạy từng thành phần riêng lẻ

### Backend (Spring Boot)

```bash
cd tour-management

# Yêu cầu: Java 21+, MySQL đang chạy
./mvnw spring-boot:run
```

Hoặc set biến môi trường kết nối database trước:
```bash
set DB_HOST=localhost
set DB_PORT=3307
set DB_NAME=tour_management_db
set DB_USERNAME=root
set DB_PASSWORD=TourAdmin@2026
./mvnw spring-boot:run
```

### Frontend (React + Vite)

```bash
cd FE/tour-management-fe

# Cài dependencies
npm install

# Chạy dev server (port 5173)
npm run dev

# Build production
npm run build
```

> Dev server mặc định proxy `/api` sang `http://localhost:8080`. Kiểm tra `vite.config.js` nếu cần điều chỉnh.

---

## Biến môi trường

| Biến              | Mặc định             | Mô tả                       |
| ----------------- | -------------------- | --------------------------- |
| `DB_NAME`         | `tour_management_db` | Tên database MySQL          |
| `DB_USERNAME`     | `root`               | Username MySQL              |
| `DB_PASSWORD`     | `root`               | Password MySQL              |
| `FRONTEND_PORT`   | `8081`               | Port expose frontend        |
| `SHOW_SQL`        | `false`              | In câu SQL ra log hay không |
| `LOG_LEVEL`       | `INFO`               | Mức log của Spring Boot     |

---

## Luồng nghiệp vụ – Thống kê doanh thu

```
GET /api/reports/tour-revenue?fromDate=...&toDate=...&status=...
          │
          ▼
  ReportController.getTourRevenue()
          │
          ▼
  ReportServiceImpl.getTourRevenueReport()
     │
     ├─ [1] Validate: fromDate <= toDate (nếu cả hai có giá trị)
     │
     ├─ [2] TourRepository.findAll()
     │       └─ Lọc in-memory: ngày, trạng thái, điểm đến
     │
     ├─ [3] BookingRepository.aggregateRevenueByTour([CONFIRMED, COMPLETED])
     │       └─ GROUP BY tour_id → { tourId, totalGuests, totalRevenue }
     │
     ├─ [4] Merge tour + revenue → List<TourRevenueDTO>
     │
     ├─ [5] Sắp xếp theo sortBy
     │
     └─ [6] Tính summary (tổng doanh thu, tổng khách, số tour)
          │
          ▼
   ApiResponse<RevenueReportDTO> { summary, tours }
```

**Cách tính doanh thu:**
- `totalAmount = numberOfGuests × unitPrice − discountAmount` (tính khi tạo booking)
- Doanh thu tour = `SUM(totalAmount)` với `status IN (CONFIRMED, COMPLETED)`
- Booking `CANCELLED` và `PENDING` không tính vào doanh thu

---

## Luồng nghiệp vụ chính

### Phân công hướng dẫn viên cho tour

```
Người dùng
   │
   ├─ [1] Xem danh sách tour  →  GET /api/tours
   │
   ├─ [2] Chọn tour           →  GET /api/tours/{id}
   │
   ├─ [3] Click "+ Phân công"
   │       └─ Tải HDV sẵn sàng  →  GET /api/guides?status=AVAILABLE
   │
   ├─ [4] Chọn HDV(s), nhập vai trò / ghi chú
   │
   └─ [5] Lưu phân công       →  POST /api/assignments
           ├─ Validate: tour chưa COMPLETED / CANCELLED
           ├─ Validate mỗi HDV: không INACTIVE / ON_LEAVE
           ├─ Kiểm tra trùng lịch
           ├─ Kiểm tra đã phân công trước đó chưa
           ├─ Lưu TourAssignment
           └─ Tự động cập nhật TourStatus: PLANNING → OPEN (nếu đủ minGuides)
```

### Logic tự động cập nhật trạng thái tour
- Sau mỗi lần lưu phân công thành công, hệ thống đếm số HDV đang hoạt động (`status != CANCELLED`) của tour.
- Nếu `activeAssignmentCount >= tour.minGuides` **và** tour đang ở trạng thái `PLANNING` → tự động chuyển sang `OPEN`.

---

## Checklist kiểm thử

### Backend
- [ ] `GET /api/reports/tour-revenue` không có filter → trả về tất cả tour
- [ ] `GET /api/reports/tour-revenue?status=COMPLETED` → chỉ trả tour COMPLETED
- [ ] `GET /api/reports/tour-revenue?fromDate=2025-10-01&toDate=2025-12-31` → lọc đúng khoảng ngày
- [ ] `GET /api/reports/tour-revenue?destination=Nha+Trang` → tìm gần đúng
- [ ] `GET /api/reports/tour-revenue?sortBy=name_asc` → sắp xếp A–Z
- [ ] `GET /api/reports/tour-revenue?fromDate=2026-12-01&toDate=2025-01-01` → lỗi 400 "Ngày bắt đầu không được sau ngày kết thúc"
- [ ] Doanh thu chỉ tính CONFIRMED + COMPLETED (không tính CANCELLED)
- [ ] Tour không có booking nào → `totalRevenue = 0`, `totalGuests = 0`
- [ ] `summary.totalRevenue = SUM(tours[i].totalRevenue)`

### Frontend
- [ ] Trang `/reports/revenue` load được, hiển thị dữ liệu mặc định
- [ ] Filter form hoạt động đúng, nhấn "Tìm kiếm" gửi đúng query params
- [ ] Nút "Đặt lại" reset filter và tải lại
- [ ] Summary cards hiển thị đúng tổng doanh thu / tổng khách / số tour
- [ ] Bar chart hiển thị top 5 tour đúng thứ tự doanh thu giảm dần
- [ ] Bảng hiển thị đầy đủ cột, footer tổng cộng đúng
- [ ] Khi không có dữ liệu → hiển thị "Không có dữ liệu phù hợp"
- [ ] Nav link "📊 Thống kê doanh thu" active đúng

---

## Tài liệu bổ sung

| Tài liệu                  | File                      | Mô tả                                        |
| ------------------------- | ------------------------- | -------------------------------------------- |
| Sơ đồ lớp                 | `class-diagram.puml`      | Toàn bộ class/interface/enum (PlantUML)      |
| Biểu đồ tuần tự           | `sequence-diagram.md`     | Chi tiết 83 bước use case phân công HDV      |
| Biểu đồ tuần tự (PlantUML)| `sequence-diagram.puml`   | Phiên bản render được bằng PlantUML          |

> Để render file `.puml`, dùng [PlantUML Online](https://www.plantuml.com/plantuml/uml/) hoặc extension PlantUML trong VS Code / IntelliJ IDEA.
