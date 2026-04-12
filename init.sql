-- Tạo database nếu chưa tồn tại
CREATE DATABASE IF NOT EXISTS tour_management_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- Cấp quyền cho user root
GRANT ALL PRIVILEGES ON tour_management_db.* TO 'root'@'%';
FLUSH PRIVILEGES;
