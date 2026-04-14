-- ============================================================
-- THIẾT KẾ CƠ SỞ DỮ LIỆU - HỆ THỐNG QUẢN LÝ TOUR DU LỊCH
-- Tour Management System - Database Schema
-- ============================================================

-- Xóa bảng nếu tồn tại (theo thứ tự phụ thuộc)
DROP TABLE IF EXISTS tour_assignments;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS tours;
DROP TABLE IF EXISTS tour_guides;
DROP TABLE IF EXISTS customers;

-- ============================================================
-- BẢNG: customers
-- Mô tả: Lưu thông tin khách hàng đặt tour
-- ============================================================
CREATE TABLE customers (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    code        VARCHAR(20)     NOT NULL COMMENT 'Mã khách hàng duy nhất (VD: CUST-001)',
    full_name   VARCHAR(200)    NOT NULL COMMENT 'Họ và tên đầy đủ',
    phone       VARCHAR(20)     NULL     COMMENT 'Số điện thoại liên hệ',
    email       VARCHAR(200)    NULL     COMMENT 'Địa chỉ email',
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm tạo bản ghi',

    CONSTRAINT pk_customers     PRIMARY KEY (id),
    CONSTRAINT uk_customers_code UNIQUE (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Bảng quản lý thông tin khách hàng';

-- ============================================================
-- BẢNG: tour_guides
-- Mô tả: Lưu thông tin hướng dẫn viên du lịch
-- ============================================================
CREATE TABLE tour_guides (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    code                VARCHAR(20)     NOT NULL COMMENT 'Mã hướng dẫn viên duy nhất (VD: HDV-001)',
    full_name           VARCHAR(100)    NOT NULL COMMENT 'Họ và tên đầy đủ',
    email               VARCHAR(100)    NULL     COMMENT 'Địa chỉ email (duy nhất)',
    phone               VARCHAR(15)     NULL     COMMENT 'Số điện thoại',
    date_of_birth       DATE            NULL     COMMENT 'Ngày sinh',
    specialization      VARCHAR(200)    NULL     COMMENT 'Chuyên môn: Beach, Mountain, City, Cultural, Eco-tourism...',
    languages           VARCHAR(200)    NULL     COMMENT 'Ngoại ngữ: English, French, Japanese, Chinese, Korean...',
    region              VARCHAR(100)    NULL     COMMENT 'Khu vực hoạt động: Miền Bắc, Miền Trung, Miền Nam, Toàn quốc',
    experience_years    INT             NOT NULL DEFAULT 0 COMMENT 'Số năm kinh nghiệm',
    status              VARCHAR(20)     NOT NULL DEFAULT 'AVAILABLE'
                            COMMENT 'Trạng thái: AVAILABLE | ON_TOUR | ON_LEAVE | INACTIVE',
    avatar_url          VARCHAR(500)    NULL     COMMENT 'URL ảnh đại diện',
    bio                 TEXT            NULL     COMMENT 'Tiểu sử / mô tả ngắn',

    CONSTRAINT pk_tour_guides       PRIMARY KEY (id),
    CONSTRAINT uk_tour_guides_code  UNIQUE (code),
    CONSTRAINT uk_tour_guides_email UNIQUE (email),
    CONSTRAINT chk_guide_status     CHECK (status IN ('AVAILABLE', 'ON_TOUR', 'ON_LEAVE', 'INACTIVE')),
    CONSTRAINT chk_experience_years CHECK (experience_years >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Bảng quản lý thông tin hướng dẫn viên du lịch';

-- ============================================================
-- BẢNG: tours
-- Mô tả: Lưu thông tin các tour du lịch
-- ============================================================
CREATE TABLE tours (
    id                      BIGINT          NOT NULL AUTO_INCREMENT,
    code                    VARCHAR(20)     NOT NULL COMMENT 'Mã tour duy nhất (VD: TOUR-001)',
    name                    VARCHAR(200)    NOT NULL COMMENT 'Tên tour',
    description             TEXT            NULL     COMMENT 'Mô tả chi tiết tour',
    destination             VARCHAR(200)    NOT NULL COMMENT 'Điểm đến',
    start_date              DATE            NOT NULL COMMENT 'Ngày khởi hành',
    end_date                DATE            NOT NULL COMMENT 'Ngày kết thúc',
    duration_days           INT             NULL     COMMENT 'Số ngày tour',
    max_guests              INT             NULL     COMMENT 'Số khách tối đa',
    current_guests          INT             NOT NULL DEFAULT 0 COMMENT 'Số khách hiện tại đã đặt',
    price                   DECIMAL(15, 2)  NULL     COMMENT 'Giá vé / người (VND)',
    status                  VARCHAR(20)     NOT NULL DEFAULT 'PLANNING'
                                COMMENT 'Trạng thái: PLANNING | OPEN | FULL | ONGOING | COMPLETED | CANCELLED',
    required_languages      VARCHAR(200)    NULL     COMMENT 'Ngôn ngữ HDV cần biết (VD: English,French)',
    required_specialization VARCHAR(200)    NULL     COMMENT 'Chuyên môn cần thiết (VD: Mountain,Eco-tourism)',
    min_guides              INT             NOT NULL DEFAULT 1 COMMENT 'Số HDV tối thiểu cần phân công',
    departure_region        VARCHAR(100)    NULL     COMMENT 'Khu vực / điểm xuất phát',

    CONSTRAINT pk_tours         PRIMARY KEY (id),
    CONSTRAINT uk_tours_code    UNIQUE (code),
    CONSTRAINT chk_tour_status  CHECK (status IN ('PLANNING', 'OPEN', 'FULL', 'ONGOING', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT chk_tour_dates   CHECK (end_date >= start_date),
    CONSTRAINT chk_max_guests   CHECK (max_guests IS NULL OR max_guests > 0),
    CONSTRAINT chk_current_guests CHECK (current_guests >= 0),
    CONSTRAINT chk_min_guides   CHECK (min_guides >= 1)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Bảng quản lý thông tin tour du lịch';

-- ============================================================
-- BẢNG: bookings
-- Mô tả: Lưu thông tin đặt tour của khách hàng
--        Mối quan hệ N-N giữa Customer và Tour
-- ============================================================
CREATE TABLE bookings (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    booking_code        VARCHAR(30)     NOT NULL COMMENT 'Mã đặt tour duy nhất (VD: BK-001)',
    tour_id             BIGINT          NOT NULL COMMENT 'FK → tours.id',
    customer_id         BIGINT          NOT NULL COMMENT 'FK → customers.id',
    booking_date        DATE            NOT NULL COMMENT 'Ngày đặt tour',
    number_of_guests    INT             NOT NULL COMMENT 'Số lượng khách trong lần đặt này',
    unit_price          DECIMAL(15, 2)  NOT NULL COMMENT 'Giá / khách tại thời điểm đặt (snapshot)',
    discount_amount     DECIMAL(15, 2)  NOT NULL DEFAULT 0.00 COMMENT 'Số tiền giảm giá',
    total_amount        DECIMAL(15, 2)  NOT NULL
                            COMMENT 'Tổng tiền = numberOfGuests * unitPrice - discountAmount',
    status              VARCHAR(20)     NOT NULL DEFAULT 'PENDING'
                            COMMENT 'Trạng thái: PENDING | CONFIRMED | CANCELLED | COMPLETED',
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm tạo booking',

    CONSTRAINT pk_bookings              PRIMARY KEY (id),
    CONSTRAINT uk_bookings_code         UNIQUE (booking_code),
    CONSTRAINT fk_bookings_tour         FOREIGN KEY (tour_id)     REFERENCES tours(id)     ON DELETE RESTRICT,
    CONSTRAINT fk_bookings_customer     FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE RESTRICT,
    CONSTRAINT chk_booking_status       CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED')),
    CONSTRAINT chk_number_of_guests     CHECK (number_of_guests > 0),
    CONSTRAINT chk_unit_price           CHECK (unit_price >= 0),
    CONSTRAINT chk_discount_amount      CHECK (discount_amount >= 0),
    CONSTRAINT chk_total_amount         CHECK (total_amount >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Bảng quản lý đặt tour - liên kết Customer và Tour';

-- ============================================================
-- BẢNG: tour_assignments
-- Mô tả: Phân công hướng dẫn viên vào tour
--        Mối quan hệ N-N giữa Tour và TourGuide
-- ============================================================
CREATE TABLE tour_assignments (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    tour_id     BIGINT          NOT NULL COMMENT 'FK → tours.id',
    guide_id    BIGINT          NOT NULL COMMENT 'FK → tour_guides.id',
    role        VARCHAR(20)     NOT NULL DEFAULT 'LEAD'
                    COMMENT 'Vai trò: LEAD (chính) | ASSISTANT (hỗ trợ)',
    note        TEXT            NULL     COMMENT 'Ghi chú phân công',
    status      VARCHAR(20)     NOT NULL DEFAULT 'ASSIGNED'
                    COMMENT 'Trạng thái: ASSIGNED | CONFIRMED | CANCELLED',
    assigned_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm phân công',
    assigned_by VARCHAR(100)    NULL     COMMENT 'Người thực hiện phân công',

    CONSTRAINT pk_tour_assignments          PRIMARY KEY (id),
    CONSTRAINT uk_tour_guide                UNIQUE (tour_id, guide_id),
    CONSTRAINT fk_assignments_tour          FOREIGN KEY (tour_id)  REFERENCES tours(id)       ON DELETE CASCADE,
    CONSTRAINT fk_assignments_guide         FOREIGN KEY (guide_id) REFERENCES tour_guides(id) ON DELETE RESTRICT,
    CONSTRAINT chk_assignment_role          CHECK (role IN ('LEAD', 'ASSISTANT')),
    CONSTRAINT chk_assignment_status        CHECK (status IN ('ASSIGNED', 'CONFIRMED', 'CANCELLED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Bảng phân công hướng dẫn viên vào tour';

-- ============================================================
-- INDEX hỗ trợ truy vấn
-- ============================================================

-- bookings: tra cứu theo tour, customer, trạng thái, ngày đặt
CREATE INDEX idx_bookings_tour_id      ON bookings (tour_id);
CREATE INDEX idx_bookings_customer_id  ON bookings (customer_id);
CREATE INDEX idx_bookings_status       ON bookings (status);
CREATE INDEX idx_bookings_booking_date ON bookings (booking_date);

-- tours: tra cứu theo trạng thái, ngày khởi hành
CREATE INDEX idx_tours_status          ON tours (status);
CREATE INDEX idx_tours_start_date      ON tours (start_date);
CREATE INDEX idx_tours_destination     ON tours (destination);

-- tour_guides: tra cứu theo trạng thái, vùng
CREATE INDEX idx_guides_status         ON tour_guides (status);
CREATE INDEX idx_guides_region         ON tour_guides (region);

-- tour_assignments: tra cứu theo tour và HDV
CREATE INDEX idx_assignments_tour_id   ON tour_assignments (tour_id);
CREATE INDEX idx_assignments_guide_id  ON tour_assignments (guide_id);
CREATE INDEX idx_assignments_status    ON tour_assignments (status);

-- ============================================================
-- TỔNG QUAN SƠ ĐỒ QUAN HỆ
-- ============================================================
--
--  customers (1) ────────────── (*) bookings (*) ────────────── (1) tours
--                                                                     |
--                                                                     | (1)
--                                                                     |
--                                                               tour_assignments
--                                                                     |
--                                                                     | (*)
--                                                                     |
--                                                              (1) tour_guides
--
--  Ghi chú:
--  • customers  : khách hàng đặt tour
--  • tours      : thông tin tour du lịch
--  • bookings   : đơn đặt tour (Customer ↔ Tour), lưu giá snapshot
--  • tour_guides: hướng dẫn viên du lịch
--  • tour_assignments: phân công HDV vào tour (Tour ↔ TourGuide)
-- ============================================================
