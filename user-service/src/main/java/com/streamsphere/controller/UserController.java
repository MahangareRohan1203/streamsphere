package com.streamspehere.controller;

import com.streamspehere.dto.CreateUserRequest;
import com.streamspehere.entity.User;
import com.streamspehere.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
//    @GetMapping("/{username}")
//    public User getUser(@PathVariable String username) {
//        return userService.getUserByUsername(username);
//    }
    
    @PostMapping
    public User createUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }
    
    @GetMapping("/internal/{username}")
    public User getInternalUser(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }
}
