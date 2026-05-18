package com.streamsphere.client;

import com.streamsphere.dto.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserClientFallback implements UserClient {
    
    @Override
    public UserResponse getUser(String username) {
        log.error("Error calling user-service for user: {}. Falling back.", username);
        // Returning null or a default user response depends on business logic.
        // For auth, returning null will cause validateUser to fail, which is appropriate
        // if we can't verify the user's existence or password.
        return null;
    }
}
