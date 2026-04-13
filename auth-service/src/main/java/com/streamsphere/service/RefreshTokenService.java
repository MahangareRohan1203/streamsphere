package com.streamsphere.service;

import com.streamsphere.dto.UserSession;

public interface RefreshTokenService {
    void save(String refreshToken, String username, String role);
    UserSession validate(String refreshToken);
    void delete(String refreshToken);
}
