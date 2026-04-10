package com.streamspehere.service;

import com.streamspehere.dto.CreateUserRequest;
import com.streamspehere.entity.User;
import com.streamspehere.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    public User createUser(CreateUserRequest request) {
        
        // Optional: check duplicate username
        userRepository.findByUsername(request.username())
                .ifPresent(u -> {
                    throw new RuntimeException("Username already exists");
                });
        
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setRole(request.role());
        
        return userRepository.save(user);
    }
}
