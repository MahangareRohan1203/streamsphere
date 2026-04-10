package com.streamspehere.dto;

public record CreateUserRequest(
        String username,
        String email,
        String role
) {}
