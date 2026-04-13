package com.streamsphere.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.streamsphere.dto.UserSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {
    
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    
    private static final String TOKEN_PREFIX = "refresh_token:";
    private static final long EXPIRE_DAYS = 7;

    public void save(String refreshToken, String username, String role) {
        UserSession session = new UserSession(username, role);
        try {
            String sessionJson = objectMapper.writeValueAsString(session);
            redisTemplate.opsForValue().set(
                TOKEN_PREFIX + refreshToken, 
                sessionJson, 
                Duration.ofDays(EXPIRE_DAYS)
            );
            log.info("Saved refresh token in Redis for user: {}", username);
        } catch (JsonProcessingException e) {
            log.error("Error serializing UserSession", e);
            throw new RuntimeException("Error saving session to Redis");
        }
    }
    
    public UserSession validate(String refreshToken) {
        String sessionJson = redisTemplate.opsForValue().get(TOKEN_PREFIX + refreshToken);
        if (sessionJson == null) {
            return null;
        }
        try {
            return objectMapper.readValue(sessionJson, UserSession.class);
        } catch (JsonProcessingException e) {
            log.error("Error deserializing UserSession", e);
            return null;
        }
    }
    
    public void delete(String refreshToken) {
        redisTemplate.delete(TOKEN_PREFIX + refreshToken);
        log.info("Deleted refresh token from Redis");
    }
}
