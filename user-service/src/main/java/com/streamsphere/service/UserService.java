package com.streamsphere.service;

import com.streamsphere.dto.CreateUserRequest;
import com.streamsphere.entity.User;
import com.streamsphere.exception.UserAlreadyExistsException;
import com.streamsphere.exception.UserNotFoundException;
import com.streamsphere.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User '" + username + "' not found"));
    }
    
    public User createUser(CreateUserRequest request) {
        
        // Optional: check duplicate username
        userRepository.findByUsername(request.username())
                .ifPresent(u -> {
                    throw new UserAlreadyExistsException("Username '" + request.username() + "' already exists");
                });
        
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setRole(request.role());
        user.setPassword(passwordEncoder.encode(request.password()));
        
        return userRepository.save(user);
    }
}
