package com.streamsphere.service;

import com.streamsphere.config.JwtUtil;
import com.streamsphere.dto.AuthResponse;
import com.streamsphere.dto.LoginRequest;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    private final JwtUtil jwtService;
    private final RefreshTokenService refreshTokenService;
    
    public AuthService(JwtUtil jwtService,
                       RefreshTokenService refreshTokenService) {
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }
    
    public AuthResponse login(LoginRequest request) {
        
        String username = request.username();
        
        // TEMP: Dummy validation (we replace later with DB)
        if (!"admin".equals(username) && !"user".equals(username)) {
            throw new RuntimeException("Invalid credentials");
        }
        
        String role = "admin".equals(username) ? "ADMIN" : "USER";
        
        String accessToken = jwtService.generateToken(username, role);
        String refreshToken = jwtService.generateRefreshToken(username);
        
        refreshTokenService.save(refreshToken, role, username);
        
        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer"
        );
    }
}
