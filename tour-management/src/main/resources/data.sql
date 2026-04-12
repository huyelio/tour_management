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
