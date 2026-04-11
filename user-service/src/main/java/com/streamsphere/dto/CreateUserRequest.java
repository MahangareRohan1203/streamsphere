package com.streamsphere.dto;

public record CreateUserRequest(
        String username,
        String password,
        String email,
        String role
) {}
