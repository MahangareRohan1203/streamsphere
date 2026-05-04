package com.streamsphere.dto;

import com.streamsphere.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user, boolean includePassword) {
        if (user == null) {
            return null;
        }
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .currentTier(user.getCurrentTier() != null ? user.getCurrentTier().name() : null)
                .password(includePassword ? user.getPassword() : null)
                .build();
    }
}
