-- =============================================
-- DỮ LIỆU MẪU - TOUR MANAGEMENT SYSTEM
-- =============================================

-- Chỉ insert nếu bảng còn rỗng (tránh duplicate khi restart)
INSERT INTO tours (code, name, description, destination, start_date, end_date, duration_days, max_guests, current_guests, price, status, required_languages, required_specialization, min_guides, departure_region)
SELECT * FROM (SELECT
    'TOUR-001', 'Khám phá Hà Nội - Hạ Long 4N3Đ',
    'Tour khám phá thủ đô Hà Nội và vịnh Hạ Long kỳ diệu với 1001 đảo đá vôi',
    'Hà Nội - Quảng Ninh',
    '2026-05-01', '2026-05-04', 4, 30, 25,
    4500000.00, 'OPEN', 'English,French', 'Cultural,Beach', 2, 'Hà Nội'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM tours WHERE code = 'TOUR-001');

INSERT INTO tours (code, name, description, destination, start_date, end_date, duration_days, max_guests, current_guests, price, status, required_languages, required_specialization, min_guides, departure_region)
SELECT * FROM (SELECT
    'TOUR-002', 'Sapa Trekking Khám Phá 3N2Đ',
    'Tour leo núi, khám phá bản làng người H''mông, Dao ở Sapa',
    'Lào Cai - Sapa',
    '2026-05-10', '2026-05-12', 3, 20, 15,
    3200000.00, 'OPEN', 'English', 'Mountain,Eco-tourism', 2, 'Hà Nội'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM tours WHERE code = 'TOUR-002');

INSERT INTO tours (code, name, description, destination, start_date, end_date, duration_days, max_guests, current_guests, price, status, required_languages, required_specialization, min_guides, departure_region)
SELECT * FROM (SELECT
    'TOUR-003', 'Đà Nẵng - Hội An - Huế 5N4Đ',
    'Khám phá miền Trung: biển Đà Nẵng, phố cổ Hội An, kinh thành Huế',
    'Đà Nẵng - Quảng Nam - Thừa Thiên Huế',
    '2026-05-20', '2026-05-24', 5, 40, 30,
    5800000.00, 'OPEN', 'English,Korean,Japanese', 'Cultural,Beach', 2, 'Đà Nẵng'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM tours WHERE code = 'TOUR-003');

INSERT INTO tours (code, name, description, destination, start_date, end_date, duration_days, max_guests, current_guests, price, status, required_languages, required_specialization, min_guides, departure_region)
SELECT * FROM (SELECT
    'TOUR-004', 'Phú Quốc Nghỉ Dưỡng 4N3Đ',
    'Nghỉ dưỡng tại đảo Phú Quốc - thiên đường biển đảo của Việt Nam',
    'Kiên Giang - Phú Quốc',
    '2026-06-01', '2026-06-04', 4, 35, 10,
    6200000.00, 'PLANNING', 'English,Chinese', 'Beach', 1, 'TP.HCM'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM tours WHERE code = 'TOUR-004');

INSERT INTO tours (code, name, description, destination, start_date, end_date, duration_days, max_guests, current_guests, price, status, required_languages, required_specialization, min_guides, departure_region)
SELECT * FROM (SELECT
    'TOUR-005', 'Cần Thơ - Miền Tây Sông Nước 3N2Đ',
    'Khám phá chợ nổi Cái Răng, miệt vườn trái cây miền Tây',
    'Cần Thơ - Tiền Giang',
    '2026-06-15', '2026-06-17', 3, 25, 5,
    2800000.00, 'PLANNING', 'English', 'Cultural,Eco-tourism', 1, 'TP.HCM'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM tours WHERE code = 'TOUR-005');

-- Dữ liệu hướng dẫn viên
INSERT INTO tour_guides (code, full_name, email, phone, specialization, languages, region, experience_years, status, bio)
SELECT * FROM (SELECT
    'HDV-001', 'Nguyễn Văn Minh', 'minh.nguyen@email.com', '0912345678',
    'Cultural,Mountain', 'English,French', 'Miền Bắc', 8, 'AVAILABLE',
    'Hướng dẫn viên giàu kinh nghiệm chuyên về văn hóa và trekking miền Bắc'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM tour_guides WHERE code = 'HDV-001');

INSERT INTO tour_guides (code, full_name, email, phone, specialization, languages, region, experience_years, status, bio)
SELECT * FROM (SELECT
    'HDV-002', 'Trần Thị Lan', 'lan.tran@email.com', '0923456789',
    'Beach,Cultural', 'English,Korean,Japanese', 'Miền Trung', 6, 'AVAILABLE',
    'Chuyên gia về du lịch biển và văn hóa miền Trung, thông thạo tiếng Nhật và Hàn'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM tour_guides WHERE code = 'HDV-002');

INSERT INTO tour_guides (code, full_name, email, phone, specialization, languages, region, experience_years, status, bio)
SELECT * FROM (SELECT
    'HDV-003', 'Lê Hoàng Nam', 'nam.le@email.com', '0934567890',
    'Eco-tourism,Mountain', 'English', 'Miền Bắc', 4, 'AVAILABLE',
    'Hướng dẫn viên trẻ năng động chuyên trekking và sinh thái'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM tour_guides WHERE code = 'HDV-003');

INSERT INTO tour_guides (code, full_name, email, phone, specialization, languages, region, experience_years, status, bio)
SELECT * FROM (SELECT
    'HDV-004', 'Phạm Thị Hoa', 'hoa.pham@email.com', '0945678901',
    'Beach,Cultural', 'English,Chinese', 'Miền Nam', 7, 'AVAILABLE',
    'Hướng dẫn viên chuyên tuyến miền Nam, Phú Quốc, thông thạo tiếng Trung'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM tour_guides WHERE code = 'HDV-004');

INSERT INTO tour_guides (code, full_name, email, phone, specialization, languages, region, experience_years, status, bio)
SELECT * FROM (SELECT
    'HDV-005', 'Võ Thanh Tùng', 'tung.vo@email.com', '0956789012',
    'Cultural,Eco-tourism', 'English,French', 'Miền Nam', 10, 'ON_TOUR',
    'Hướng dẫn viên kỳ cựu tại miền Nam với 10 năm kinh nghiệm'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM tour_guides WHERE code = 'HDV-005');

INSERT INTO tour_guides (code, full_name, email, phone, specialization, languages, region, experience_years, status, bio)
SELECT * FROM (SELECT
    'HDV-006', 'Ngô Thị Mai', 'mai.ngo@email.com', '0967890123',
    'Mountain,Cultural', 'English,Korean', 'Toàn quốc', 5, 'AVAILABLE',
    'Hướng dẫn viên linh hoạt có thể dẫn tour toàn quốc'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM tour_guides WHERE code = 'HDV-006');

INSERT INTO tour_guides (code, full_name, email, phone, specialization, languages, region, experience_years, status, bio)
SELECT * FROM (SELECT
    'HDV-007', 'Đặng Văn Khoa', 'khoa.dang@email.com', '0978901234',
    'Beach', 'English,Japanese', 'Miền Trung', 3, 'INACTIVE',
    'Tạm thời ngưng hoạt động'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM tour_guides WHERE code = 'HDV-007');

-- =============================================
-- TOUR BỔ SUNG (quá khứ – có doanh thu)
-- =============================================

INSERT INTO tours (code, name, description, destination, start_date, end_date, duration_days, max_guests, current_guests, price, status, required_languages, required_specialization, min_guides, departure_region)
SELECT * FROM (SELECT
    'TOUR-006', 'Nha Trang Biển Xanh 4N3Đ',
    'Tắm biển Nha Trang, lặn ngắm san hô, thăm tháp Chàm Po Nagar',
    'Khánh Hòa - Nha Trang',
    '2025-11-01', '2025-11-04', 4, 40 AS max_guests, 40 AS current_guests,
    5500000.00, 'COMPLETED', 'English,Korean', 'Beach,Cultural', 2, 'TP.HCM'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM tours WHERE code = 'TOUR-006');

INSERT INTO tours (code, name, description, destination, start_date, end_date, duration_days, max_guests, current_guests, price, status, required_languages, required_specialization, min_guides, departure_region)
SELECT * FROM (SELECT
    'TOUR-007', 'Đà Lạt Lãng Mạn 4N3Đ',
    'Thành phố ngàn hoa, thác Prenn, hồ Xuân Hương, lang biang',
    'Lâm Đồng - Đà Lạt',
    '2025-12-10', '2025-12-13', 4, 30, 28,
    4200000.00, 'COMPLETED', 'English,French', 'Cultural,Eco-tourism', 2, 'TP.HCM'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM tours WHERE code = 'TOUR-007');

INSERT INTO tours (code, name, description, destination, start_date, end_date, duration_days, max_guests, current_guests, price, status, required_languages, required_specialization, min_guides, departure_region)
SELECT * FROM (SELECT
    'TOUR-008', 'Hà Giang Cao Nguyên Đá 5N4Đ',
    'Trekking cao nguyên đá Đồng Văn, đèo Mã Pì Lèng hùng vĩ',
    'Hà Giang - Đồng Văn',
    '2026-01-05', '2026-01-09', 5, 20, 18,
    6800000.00, 'COMPLETED', 'English', 'Mountain,Eco-tourism', 2, 'Hà Nội'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM tours WHERE code = 'TOUR-008');

INSERT INTO tours (code, name, description, destination, start_date, end_date, duration_days, max_guests, current_guests, price, status, required_languages, required_specialization, min_guides, departure_region)
SELECT * FROM (SELECT
    'TOUR-009', 'Phong Nha Kẻ Bàng 3N2Đ',
    'Khám phá động Phong Nha, động Thiên Đường – di sản UNESCO',
    'Quảng Bình - Phong Nha',
    '2026-02-14', '2026-02-16', 3, 25, 20,
    3900000.00, 'COMPLETED', 'English,French', 'Cultural,Eco-tourism', 1, 'Đà Nẵng'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM tours WHERE code = 'TOUR-009');

INSERT INTO tours (code, name, description, destination, start_date, end_date, duration_days, max_guests, current_guests, price, status, required_languages, required_specialization, min_guides, departure_region)
SELECT * FROM (SELECT
    'TOUR-010', 'TP.HCM City Tour Sài Gòn 2N1Đ',
    'Bến Nhà Rồng, Bảo tàng Chứng tích chiến tranh, Chợ Bến Thành',
    'TP. Hồ Chí Minh',
    '2026-01-20', '2026-01-21', 2 AS duration_days, 50, 48,
    1800000.00, 'COMPLETED', 'English,Chinese,Korean', 'Cultural', 2 AS min_guides, 'TP.HCM'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM tours WHERE code = 'TOUR-010');

INSERT INTO tours (code, name, description, destination, start_date, end_date, duration_days, max_guests, current_guests, price, status, required_languages, required_specialization, min_guides, departure_region)
SELECT * FROM (SELECT
    'TOUR-011', 'Mũi Né Cồn Cát 3N2Đ',
    'Đồi cát bay, suối Tiên, làng chài Mũi Né',
    'Bình Thuận - Mũi Né',
    '2026-03-01', '2026-03-03', 3, 30, 0,
    3100000.00, 'CANCELLED', 'English', 'Beach', 1, 'TP.HCM'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM tours WHERE code = 'TOUR-011');

INSERT INTO tours (code, name, description, destination, start_date, end_date, duration_days, max_guests, current_guests, price, status, required_languages, required_specialization, min_guides, departure_region)
SELECT * FROM (SELECT
    'TOUR-012', 'Côn Đảo Hòn Ngọc 4N3Đ',
    'Khám phá Côn Đảo – lặn biển, rùa đẻ trứng, khu di tích lịch sử',
    'Bà Rịa - Vũng Tàu - Côn Đảo',
    '2026-07-10', '2026-07-13', 4, 20, 8,
    8500000.00, 'OPEN', 'English', 'Beach,Cultural', 1, 'TP.HCM'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM tours WHERE code = 'TOUR-012');

INSERT INTO tours (code, name, description, destination, start_date, end_date, duration_days, max_guests, current_guests, price, status, required_languages, required_specialization, min_guides, departure_region)
SELECT * FROM (SELECT
    'TOUR-013', 'Hội An Đèn Lồng Rực Rỡ 3N2Đ',
    'Đêm hoa đăng, làng rau Trà Quế, thánh địa Mỹ Sơn',
    'Quảng Nam - Hội An',
    '2026-04-05', '2026-04-07', 3, 35, 32,
    4000000.00, 'ONGOING', 'English,Korean,Japanese', 'Cultural', 2, 'Đà Nẵng'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM tours WHERE code = 'TOUR-013');

-- =============================================
-- KHÁCH HÀNG (20 khách hàng)
-- =============================================

INSERT INTO customers (code, full_name, phone, email) SELECT * FROM (SELECT 'CUST-001', 'Nguyễn Thị Hương', '0901111001', 'huong.nt@gmail.com') AS tmp WHERE NOT EXISTS (SELECT 1 FROM customers WHERE code = 'CUST-001');
INSERT INTO customers (code, full_name, phone, email) SELECT * FROM (SELECT 'CUST-002', 'Trần Văn Hải', '0901111002', 'hai.tv@gmail.com') AS tmp WHERE NOT EXISTS (SELECT 1 FROM customers WHERE code = 'CUST-002');
INSERT INTO customers (code, full_name, phone, email) SELECT * FROM (SELECT 'CUST-003', 'Lê Thị Bích', '0901111003', 'bich.lt@gmail.com') AS tmp WHERE NOT EXISTS (SELECT 1 FROM customers WHERE code = 'CUST-003');
INSERT INTO customers (code, full_name, phone, email) SELECT * FROM (SELECT 'CUST-004', 'Phạm Quốc Toàn', '0901111004', 'toan.pq@gmail.com') AS tmp WHERE NOT EXISTS (SELECT 1 FROM customers WHERE code = 'CUST-004');
INSERT INTO customers (code, full_name, phone, email) SELECT * FROM (SELECT 'CUST-005', 'Hoàng Minh Châu', '0901111005', 'chau.hm@gmail.com') AS tmp WHERE NOT EXISTS (SELECT 1 FROM customers WHERE code = 'CUST-005');
INSERT INTO customers (code, full_name, phone, email) SELECT * FROM (SELECT 'CUST-006', 'Võ Thị Ngọc', '0901111006', 'ngoc.vt@gmail.com') AS tmp WHERE NOT EXISTS (SELECT 1 FROM customers WHERE code = 'CUST-006');
INSERT INTO customers (code, full_name, phone, email) SELECT * FROM (SELECT 'CUST-007', 'Đặng Văn Long', '0901111007', 'long.dv@gmail.com') AS tmp WHERE NOT EXISTS (SELECT 1 FROM customers WHERE code = 'CUST-007');
INSERT INTO customers (code, full_name, phone, email) SELECT * FROM (SELECT 'CUST-008', 'Bùi Thị Thu', '0901111008', 'thu.bt@gmail.com') AS tmp WHERE NOT EXISTS (SELECT 1 FROM customers WHERE code = 'CUST-008');
INSERT INTO customers (code, full_name, phone, email) SELECT * FROM (SELECT 'CUST-009', 'Đinh Công Sơn', '0901111009', 'son.dc@gmail.com') AS tmp WHERE NOT EXISTS (SELECT 1 FROM customers WHERE code = 'CUST-009');
INSERT INTO customers (code, full_name, phone, email) SELECT * FROM (SELECT 'CUST-010', 'Ngô Thị Phương', '0901111010', 'phuong.nt@gmail.com') AS tmp WHERE NOT EXISTS (SELECT 1 FROM customers WHERE code = 'CUST-010');
INSERT INTO customers (code, full_name, phone, email) SELECT * FROM (SELECT 'CUST-011', 'Lý Văn Đức', '0901111011', 'duc.lv@gmail.com') AS tmp WHERE NOT EXISTS (SELECT 1 FROM customers WHERE code = 'CUST-011');
INSERT INTO customers (code, full_name, phone, email) SELECT * FROM (SELECT 'CUST-012', 'Trịnh Thị Xuân', '0901111012', 'xuan.tt@gmail.com') AS tmp WHERE NOT EXISTS (SELECT 1 FROM customers WHERE code = 'CUST-012');
INSERT INTO customers (code, full_name, phone, email) SELECT * FROM (SELECT 'CUST-013', 'Phan Minh Khôi', '0901111013', 'khoi.pm@gmail.com') AS tmp WHERE NOT EXISTS (SELECT 1 FROM customers WHERE code = 'CUST-013');
INSERT INTO customers (code, full_name, phone, email) SELECT * FROM (SELECT 'CUST-014', 'Mai Thị Linh', '0901111014', 'linh.mt@gmail.com') AS tmp WHERE NOT EXISTS (SELECT 1 FROM customers WHERE code = 'CUST-014');
INSERT INTO customers (code, full_name, phone, email) SELECT * FROM (SELECT 'CUST-015', 'Cao Văn Tuấn', '0901111015', 'tuan.cv@gmail.com') AS tmp WHERE NOT EXISTS (SELECT 1 FROM customers WHERE code = 'CUST-015');
INSERT INTO customers (code, full_name, phone, email) SELECT * FROM (SELECT 'CUST-016', 'Dương Thị Hồng', '0901111016', 'hong.dt@gmail.com') AS tmp WHERE NOT EXISTS (SELECT 1 FROM customers WHERE code = 'CUST-016');
INSERT INTO customers (code, full_name, phone, email) SELECT * FROM (SELECT 'CUST-017', 'Tô Văn Phúc', '0901111017', 'phuc.tv@gmail.com') AS tmp WHERE NOT EXISTS (SELECT 1 FROM customers WHERE code = 'CUST-017');
INSERT INTO customers (code, full_name, phone, email) SELECT * FROM (SELECT 'CUST-018', 'Vũ Thị Thanh', '0901111018', 'thanh.vt@gmail.com') AS tmp WHERE NOT EXISTS (SELECT 1 FROM customers WHERE code = 'CUST-018');
INSERT INTO customers (code, full_name, phone, email) SELECT * FROM (SELECT 'CUST-019', 'Hồ Văn Khánh', '0901111019', 'khanh.hv@gmail.com') AS tmp WHERE NOT EXISTS (SELECT 1 FROM customers WHERE code = 'CUST-019');
INSERT INTO customers (code, full_name, phone, email) SELECT * FROM (SELECT 'CUST-020', 'Lưu Thị Yến', '0901111020', 'yen.lt@gmail.com') AS tmp WHERE NOT EXISTS (SELECT 1 FROM customers WHERE code = 'CUST-020');

-- =============================================
-- BOOKING
-- Quy tắc: totalAmount = numberOfGuests * unitPrice - discountAmount
-- Chỉ CONFIRMED + COMPLETED mới tính vào doanh thu
-- =============================================

-- TOUR-006 Nha Trang (COMPLETED) – 7 booking, 6 tính doanh thu
INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-001', (SELECT id FROM tours WHERE code='TOUR-006'), (SELECT id FROM customers WHERE code='CUST-001'), '2025-10-01', 2, 5500000.00, 0.00, 11000000.00, 'COMPLETED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-001');

INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-002', (SELECT id FROM tours WHERE code='TOUR-006'), (SELECT id FROM customers WHERE code='CUST-002'), '2025-10-02', 3, 5500000.00, 500000.00, 16000000.00, 'COMPLETED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-002');

INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-003', (SELECT id FROM tours WHERE code='TOUR-006'), (SELECT id FROM customers WHERE code='CUST-003'), '2025-10-03', 4, 5500000.00, 1000000.00, 21000000.00, 'CONFIRMED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-003');

INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-004', (SELECT id FROM tours WHERE code='TOUR-006'), (SELECT id FROM customers WHERE code='CUST-004'), '2025-10-05', 2, 5500000.00, 0.00, 11000000.00, 'COMPLETED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-004');

INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-005', (SELECT id FROM tours WHERE code='TOUR-006'), (SELECT id FROM customers WHERE code='CUST-005'), '2025-10-06', 5, 5500000.00, 2000000.00, 25500000.00, 'COMPLETED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-005');

INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-006', (SELECT id FROM tours WHERE code='TOUR-006'), (SELECT id FROM customers WHERE code='CUST-006'), '2025-10-08', 4, 5500000.00, 0.00, 22000000.00, 'COMPLETED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-006');

INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-007', (SELECT id FROM tours WHERE code='TOUR-006'), (SELECT id FROM customers WHERE code='CUST-007'), '2025-10-10', 2, 5500000.00, 0.00, 11000000.00, 'CANCELLED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-007');

-- TOUR-007 Đà Lạt (COMPLETED) – 6 booking, 5 tính doanh thu
INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-008', (SELECT id FROM tours WHERE code='TOUR-007'), (SELECT id FROM customers WHERE code='CUST-008'), '2025-11-15', 3, 4200000.00, 0.00, 12600000.00, 'COMPLETED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-008');

INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-009', (SELECT id FROM tours WHERE code='TOUR-007'), (SELECT id FROM customers WHERE code='CUST-009'), '2025-11-16', 2, 4200000.00, 500000.00, 7900000.00, 'COMPLETED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-009');

INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-010', (SELECT id FROM tours WHERE code='TOUR-007'), (SELECT id FROM customers WHERE code='CUST-010'), '2025-11-18', 4, 4200000.00, 1000000.00, 15800000.00, 'CONFIRMED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-010');

INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-011', (SELECT id FROM tours WHERE code='TOUR-007'), (SELECT id FROM customers WHERE code='CUST-011'), '2025-11-20', 5, 4200000.00, 2000000.00, 19000000.00, 'COMPLETED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-011');

INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-012', (SELECT id FROM tours WHERE code='TOUR-007'), (SELECT id FROM customers WHERE code='CUST-012'), '2025-11-22', 2, 4200000.00, 0.00, 8400000.00, 'COMPLETED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-012');

INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-013', (SELECT id FROM tours WHERE code='TOUR-007'), (SELECT id FROM customers WHERE code='CUST-013'), '2025-11-25', 3, 4200000.00, 0.00, 12600000.00, 'CANCELLED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-013');

-- TOUR-008 Hà Giang (COMPLETED) – 4 booking
INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-014', (SELECT id FROM tours WHERE code='TOUR-008'), (SELECT id FROM customers WHERE code='CUST-014'), '2025-12-10', 2, 6800000.00, 0.00, 13600000.00, 'COMPLETED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-014');

INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-015', (SELECT id FROM tours WHERE code='TOUR-008'), (SELECT id FROM customers WHERE code='CUST-015'), '2025-12-12', 3, 6800000.00, 1500000.00, 18900000.00, 'COMPLETED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-015');

INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-016', (SELECT id FROM tours WHERE code='TOUR-008'), (SELECT id FROM customers WHERE code='CUST-016'), '2025-12-14', 4, 6800000.00, 2000000.00, 25200000.00, 'CONFIRMED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-016');

INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-017', (SELECT id FROM tours WHERE code='TOUR-008'), (SELECT id FROM customers WHERE code='CUST-017'), '2025-12-15', 2, 6800000.00, 0.00, 13600000.00, 'CANCELLED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-017');

-- TOUR-009 Phong Nha (COMPLETED) – 4 booking
INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-018', (SELECT id FROM tours WHERE code='TOUR-009'), (SELECT id FROM customers WHERE code='CUST-018'), '2026-01-20', 2, 3900000.00, 0.00, 7800000.00, 'COMPLETED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-018');

INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-019', (SELECT id FROM tours WHERE code='TOUR-009'), (SELECT id FROM customers WHERE code='CUST-019'), '2026-01-22', 3, 3900000.00, 500000.00, 11200000.00, 'COMPLETED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-019');

INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-020', (SELECT id FROM tours WHERE code='TOUR-009'), (SELECT id FROM customers WHERE code='CUST-020'), '2026-01-24', 4, 3900000.00, 1000000.00, 14600000.00, 'CONFIRMED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-020');

INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-021', (SELECT id FROM tours WHERE code='TOUR-009'), (SELECT id FROM customers WHERE code='CUST-001'), '2026-01-26', 2, 3900000.00, 0.00, 7800000.00, 'CANCELLED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-021');

-- TOUR-010 TP.HCM City Tour (COMPLETED) – doanh thu cao nhất
INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-022', (SELECT id FROM tours WHERE code='TOUR-010'), (SELECT id FROM customers WHERE code='CUST-002'), '2026-01-05', 5, 1800000.00, 0.00, 9000000.00, 'COMPLETED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-022');

INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-023', (SELECT id FROM tours WHERE code='TOUR-010'), (SELECT id FROM customers WHERE code='CUST-003'), '2026-01-06', 6, 1800000.00, 1000000.00, 9800000.00, 'COMPLETED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-023');

INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-024', (SELECT id FROM tours WHERE code='TOUR-010'), (SELECT id FROM customers WHERE code='CUST-004'), '2026-01-07', 8, 1800000.00, 2000000.00, 12400000.00, 'CONFIRMED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-024');

INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-025', (SELECT id FROM tours WHERE code='TOUR-010'), (SELECT id FROM customers WHERE code='CUST-005'), '2026-01-08', 4, 1800000.00, 0.00, 7200000.00, 'COMPLETED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-025');

INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-026', (SELECT id FROM tours WHERE code='TOUR-010'), (SELECT id FROM customers WHERE code='CUST-006'), '2026-01-09', 7, 1800000.00, 1500000.00, 11100000.00, 'COMPLETED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-026');

INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-027', (SELECT id FROM tours WHERE code='TOUR-010'), (SELECT id FROM customers WHERE code='CUST-007'), '2026-01-10', 6, 1800000.00, 0.00, 10800000.00, 'COMPLETED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-027');

INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-028', (SELECT id FROM tours WHERE code='TOUR-010'), (SELECT id FROM customers WHERE code='CUST-008'), '2026-01-11', 3, 1800000.00, 0.00, 5400000.00, 'CANCELLED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-028');

-- TOUR-001 Hà Nội - Hạ Long (OPEN) – có booking đang chờ/xác nhận
INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-029', (SELECT id FROM tours WHERE code='TOUR-001'), (SELECT id FROM customers WHERE code='CUST-009'), '2026-04-01', 2, 4500000.00, 0.00, 9000000.00, 'CONFIRMED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-029');

INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-030', (SELECT id FROM tours WHERE code='TOUR-001'), (SELECT id FROM customers WHERE code='CUST-010'), '2026-04-02', 3, 4500000.00, 500000.00, 13000000.00, 'CONFIRMED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-030');

INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-031', (SELECT id FROM tours WHERE code='TOUR-001'), (SELECT id FROM customers WHERE code='CUST-011'), '2026-04-03', 4, 4500000.00, 0.00, 18000000.00, 'PENDING') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-031');

INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-032', (SELECT id FROM tours WHERE code='TOUR-001'), (SELECT id FROM customers WHERE code='CUST-012'), '2026-04-04', 2, 4500000.00, 0.00, 9000000.00, 'CANCELLED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-032');

-- TOUR-003 Đà Nẵng - Hội An (OPEN)
INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-033', (SELECT id FROM tours WHERE code='TOUR-003'), (SELECT id FROM customers WHERE code='CUST-013'), '2026-04-10', 3, 5800000.00, 1000000.00, 16400000.00, 'CONFIRMED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-033');

INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-034', (SELECT id FROM tours WHERE code='TOUR-003'), (SELECT id FROM customers WHERE code='CUST-014'), '2026-04-11', 2, 5800000.00, 0.00, 11600000.00, 'CONFIRMED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-034');

INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-035', (SELECT id FROM tours WHERE code='TOUR-003'), (SELECT id FROM customers WHERE code='CUST-015'), '2026-04-12', 5, 5800000.00, 2000000.00, 27000000.00, 'PENDING') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-035');

-- TOUR-002 Sapa (OPEN)
INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-036', (SELECT id FROM tours WHERE code='TOUR-002'), (SELECT id FROM customers WHERE code='CUST-016'), '2026-04-15', 2, 3200000.00, 0.00, 6400000.00, 'CONFIRMED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-036');

INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-037', (SELECT id FROM tours WHERE code='TOUR-002'), (SELECT id FROM customers WHERE code='CUST-017'), '2026-04-16', 3, 3200000.00, 500000.00, 9100000.00, 'CONFIRMED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-037');

-- TOUR-011 Mũi Né (CANCELLED) – có booking đã hủy
INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-038', (SELECT id FROM tours WHERE code='TOUR-011'), (SELECT id FROM customers WHERE code='CUST-018'), '2026-02-01', 2, 3100000.00, 0.00, 6200000.00, 'CANCELLED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-038');

INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-039', (SELECT id FROM tours WHERE code='TOUR-011'), (SELECT id FROM customers WHERE code='CUST-019'), '2026-02-02', 3, 3100000.00, 0.00, 9300000.00, 'CANCELLED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-039');

-- TOUR-012 Côn Đảo (OPEN)
INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-040', (SELECT id FROM tours WHERE code='TOUR-012'), (SELECT id FROM customers WHERE code='CUST-020'), '2026-04-10', 2, 8500000.00, 0.00, 17000000.00, 'CONFIRMED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-040');

INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-041', (SELECT id FROM tours WHERE code='TOUR-012'), (SELECT id FROM customers WHERE code='CUST-001'), '2026-04-11', 3, 8500000.00, 1500000.00, 24000000.00, 'PENDING') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-041');

-- TOUR-013 Hội An (ONGOING)
INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-042', (SELECT id FROM tours WHERE code='TOUR-013'), (SELECT id FROM customers WHERE code='CUST-002'), '2026-03-15', 4, 4000000.00, 0.00, 16000000.00, 'CONFIRMED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-042');

INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-043', (SELECT id FROM tours WHERE code='TOUR-013'), (SELECT id FROM customers WHERE code='CUST-003'), '2026-03-16', 3, 4000000.00, 500000.00, 11500000.00, 'CONFIRMED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-043');

INSERT INTO bookings (booking_code, tour_id, customer_id, booking_date, number_of_guests, unit_price, discount_amount, total_amount, status)
SELECT * FROM (SELECT 'BK-044', (SELECT id FROM tours WHERE code='TOUR-013'), (SELECT id FROM customers WHERE code='CUST-004'), '2026-03-18', 2, 4000000.00, 0.00, 8000000.00, 'CONFIRMED') AS tmp WHERE NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK-044');
