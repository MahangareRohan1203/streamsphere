package com.streamsphere.controller;

import com.streamsphere.dto.CreateUserRequest;
import com.streamsphere.dto.UserMapper;
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

    @Autowired
    private UserMapper userMapper;
    
    @PostMapping
    public UserResponse createUser(@RequestBody CreateUserRequest request) {
        User user = userService.createUser(request);
        return userMapper.toResponse(user, false); // PUBLIC: No password
    }
    
    @GetMapping("/internal/{username}")
    public UserResponse getInternalUser(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        return userMapper.toResponse(user, true); // INTERNAL: Include password
    }
}
