package com.streamsphere.service;

import com.streamsphere.dto.UserSession;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RefreshTokenService {
    
    private final Map<String, UserSession> refreshTokenStore = new ConcurrentHashMap<>();
    
    public void save(String refreshToken, String username, String role) {
        refreshTokenStore.put(refreshToken, new UserSession(username, role));
    }
    
    public UserSession validate(String refreshToken) {
        return refreshTokenStore.get(refreshToken);
    }
    
    public void delete(String refreshToken) {
        refreshTokenStore.remove(refreshToken);
    }
}
