package com.example.tourmanagement.service;

import com.example.tourmanagement.model.User;

public interface AuthService {
    User register(User user);
    User login(String username, String password);
}
