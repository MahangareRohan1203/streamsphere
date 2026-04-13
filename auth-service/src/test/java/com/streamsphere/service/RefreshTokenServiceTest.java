package com.streamsphere.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.streamsphere.dto.UserSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void save_Success() throws JsonProcessingException {
        String token = "test-token";
        String username = "user1";
        String role = "USER";
        String sessionJson = "{\"username\":\"user1\",\"role\":\"USER\"}";

        when(objectMapper.writeValueAsString(any(UserSession.class))).thenReturn(sessionJson);

        refreshTokenService.save(token, username, role);

        verify(valueOperations).set(eq("refresh_token:" + token), eq(sessionJson), any(Duration.class));
    }

    @Test
    void validate_Success() throws JsonProcessingException {
        String token = "test-token";
        String sessionJson = "{\"username\":\"user1\",\"role\":\"USER\"}";
        UserSession expectedSession = new UserSession("user1", "USER");

        when(valueOperations.get("refresh_token:" + token)).thenReturn(sessionJson);
        when(objectMapper.readValue(sessionJson, UserSession.class)).thenReturn(expectedSession);

        UserSession result = refreshTokenService.validate(token);

        assertNotNull(result);
        assertEquals("user1", result.username());
        assertEquals("USER", result.role());
    }

    @Test
    void validate_TokenNotFound() {
        String token = "invalid-token";
        when(valueOperations.get("refresh_token:" + token)).thenReturn(null);

        UserSession result = refreshTokenService.validate(token);

        assertNull(result);
    }
}
