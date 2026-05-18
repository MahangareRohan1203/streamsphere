package com.streamsphere.service;

import com.streamsphere.client.UserClient;
import com.streamsphere.config.JwtUtil;
import com.streamsphere.dto.AuthResponse;
import com.streamsphere.dto.LoginRequest;
import com.streamsphere.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtUtil jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserClient userClient;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void login_Success() {
        // Arrange
        LoginRequest request = new LoginRequest("user1", "password");
        UserResponse user = new UserResponse();
        user.setUsername("user1");
        user.setPassword("hashed_password");
        user.setRole("USER");

        when(userClient.getUser("user1")).thenReturn(user);
        when(passwordEncoder.matches("password", "hashed_password")).thenReturn(true);
        when(jwtService.generateToken("user1", "USER")).thenReturn("access_token");
        when(jwtService.generateRefreshToken("user1")).thenReturn("refresh_token");

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("access_token", response.accessToken());
        assertEquals("refresh_token", response.refreshToken());
        verify(refreshTokenService).save("refresh_token", "user1", "USER");
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        // Arrange
        LoginRequest request = new LoginRequest("nonexistent", "password");
        when(userClient.getUser("nonexistent")).thenReturn(null);

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> authService.login(request));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void login_WrongPassword_ThrowsException() {
        // Arrange
        LoginRequest request = new LoginRequest("user1", "wrong_password");
        UserResponse user = new UserResponse();
        user.setUsername("user1");
        user.setPassword("hashed_password");

        when(userClient.getUser("user1")).thenReturn(user);
        when(passwordEncoder.matches("wrong_password", "hashed_password")).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void validateUser_UserClientReturnsNull_ThrowsExceptionInLogin() {
        // Arrange
        LoginRequest request = new LoginRequest("user1", "password");
        // Simulating circuit breaker fallback returning null
        when(userClient.getUser("user1")).thenReturn(null);

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> authService.login(request));
    }
}
