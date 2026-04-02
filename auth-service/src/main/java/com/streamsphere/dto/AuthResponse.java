package com.streamsphere.dto;

public record AuthResponse(String accessToken, String refreshToken, String tokenType) {
}
