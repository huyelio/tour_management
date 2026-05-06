package com.example.tourmanagement.service.impl;

import com.example.tourmanagement.exception.BusinessException;
import com.example.tourmanagement.model.User;
import com.example.tourmanagement.repository.UserRepository;
import com.example.tourmanagement.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    // BCryptPasswordEncoder không cần Spring Security filter chain
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User register(User request) {
        if (request == null) {
            throw new BusinessException("Dữ liệu đăng ký không hợp lệ");
        }

        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new BusinessException("Username không được để trống");
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new BusinessException("Password không được để trống");
        }

        // Kiểm tra username đã tồn tại chưa
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username '" + request.getUsername() + "' đã tồn tại");
        }

        // Tạo và lưu user mới với password đã được mã hóa
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .build();

        return userRepository.save(user);
    }

    @Override
    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new BusinessException("Username và password không được để trống");
        }

        // Tìm user theo username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Username hoặc password không đúng"));

        // So sánh password nhập vào với hash đã lưu
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("Username hoặc password không đúng");
        }

        return user;
    }
}
