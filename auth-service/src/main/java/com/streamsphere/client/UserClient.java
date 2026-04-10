package com.streamsphere.client;

import com.streamsphere.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient {
    
    @GetMapping("/users/internal/{username}")
    UserResponse getUser(@PathVariable String username);
}
