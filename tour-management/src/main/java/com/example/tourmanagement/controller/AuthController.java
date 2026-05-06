package com.example.tourmanagement.controller;

import com.example.tourmanagement.dto.response.ApiResponse;
import com.example.tourmanagement.model.User;
import com.example.tourmanagement.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(
            @RequestBody User request) {
        User user = authService.register(request);
        return ResponseEntity.ok(ApiResponse.ok("Đăng ký thành công", user));
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<User>> login(
            @RequestBody Map<String, Object> body) {
        String username = body == null ? null : (String) body.get("username");
        String password = body == null ? null : (String) body.get("password");
        User user = authService.login(username, password);
        return ResponseEntity.ok(ApiResponse.ok("Đăng nhập thành công", user));
    }
}
