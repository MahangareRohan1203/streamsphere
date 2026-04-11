package com.streamsphere.controller;

import com.streamsphere.dto.CreateUserRequest;
import com.streamsphere.dto.UserResponse;
import com.streamsphere.entity.User;
import com.streamsphere.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    public UserResponse createUser(@RequestBody CreateUserRequest request) {
        User user = userService.createUser(request);
        return mapToResponse(user, false); // PUBLIC: No password
    }
    
    @GetMapping("/internal/{username}")
    public UserResponse getInternalUser(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        return mapToResponse(user, true); // INTERNAL: Include password
    }

    private UserResponse mapToResponse(User user, boolean includePassword) {
        return UserResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .password(includePassword ? user.getPassword() : null)
                .build();
    }
}
