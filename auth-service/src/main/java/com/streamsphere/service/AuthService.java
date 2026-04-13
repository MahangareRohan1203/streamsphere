package com.streamsphere.service;

import com.streamsphere.dto.AuthResponse;
import com.streamsphere.dto.LoginRequest;
import com.streamsphere.dto.UserResponse;

public interface AuthService {
    UserResponse validateUser(String username);
    AuthResponse login(LoginRequest request);
}
