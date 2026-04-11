package com.streamsphere.service;

import com.streamsphere.client.UserClient;
import com.streamsphere.config.JwtUtil;
import com.streamsphere.dto.AuthResponse;
import com.streamsphere.dto.LoginRequest;
import com.streamsphere.dto.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {
    
    private final JwtUtil jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserClient userClient;
    private final PasswordEncoder passwordEncoder;
    
    
    public AuthService(JwtUtil jwtService,
                       RefreshTokenService refreshTokenService,
                       UserClient userClient,
                       PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.userClient = userClient;
        this.passwordEncoder = passwordEncoder;
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
        
        // Verify password
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.error("Password mismatch for user: {}", username);
            throw new RuntimeException("Invalid username or password");
        }
        
        String role = user.getRole();
        
        String accessToken = jwtService.generateToken(username, role);
        String refreshToken = jwtService.generateRefreshToken(username);
        
        refreshTokenService.save(refreshToken, username, role);
        
        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer"
        );
    }
}
