package com.streamsphere.service;

import com.streamsphere.client.UserClient;
import com.streamsphere.config.JwtUtil;
import com.streamsphere.dto.AuthResponse;
import com.streamsphere.dto.LoginRequest;
import com.streamsphere.dto.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {
    
    private final JwtUtil jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserClient userClient;
    
    
    public AuthService(JwtUtil jwtService,
                       RefreshTokenService refreshTokenService,
                       UserClient userClient) {
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.userClient = userClient;
    }
    
    public UserResponse validateUser(String username) {
        log.info("validating");
        return userClient.getUser(username);
    }
    
    public AuthResponse login(LoginRequest request) {
        
        String username = request.username();
        log.info("=================="+username);
        UserResponse user = validateUser(username);
        log.info("validated");
        if (user == null) throw new UsernameNotFoundException("User not found");
        
        String role = user.getRole();
        
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
