package com.streamsphere.controller;

import com.streamsphere.config.JwtUtil;
import com.streamsphere.dto.AuthResponse;
import com.streamsphere.dto.LoginRequest;
import com.streamsphere.dto.LogoutRequest;
import com.streamsphere.dto.UserSession;
import com.streamsphere.service.AuthService;
import com.streamsphere.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private RefreshTokenService refreshTokenService;
    
    @Autowired
    private AuthService authService;
    
    
    @GetMapping("/test")
    public String test() {
        return "test";
    }
    
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
    
    
    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody Map<String, String> request) {
        
        String refreshToken = request.get("refreshToken");
        
        UserSession session = refreshTokenService.validate(refreshToken);
        
        if (session == null) {
            throw new RuntimeException("Invalid refresh token");
        }
        
        // Invalidating the old refresh token
        refreshTokenService.delete(refreshToken);
        
        String newAccessToken = jwtUtil.generateToken(
                session.username(),
                session.role()
        );
        
        String newRefreshToken = jwtUtil.generateRefreshToken(
                session.username()
        );
        
        // ✅ Save new refresh token
        refreshTokenService.save(
                newRefreshToken,
                session.username(),
                session.role()
        );
        
        return new AuthResponse(
                newAccessToken,
                newRefreshToken,
                "Bearer"
        );
    }
    
    
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody LogoutRequest request) {
        
        refreshTokenService.delete(request.refreshToken());
        
        return ResponseEntity.ok("Logged out Successfully");
    }
    
}
