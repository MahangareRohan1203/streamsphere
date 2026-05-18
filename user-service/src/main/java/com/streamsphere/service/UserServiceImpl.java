package com.streamsphere.service;

import com.streamsphere.dto.CreateUserRequest;
import com.streamsphere.entity.User;
import com.streamsphere.exception.UserAlreadyExistsException;
import com.streamsphere.exception.UserNotFoundException;
import com.streamsphere.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User '" + username + "' not found"));
    }
    
    @Transactional
    public User createUser(CreateUserRequest request) {
        log.info("Attempting to create user: {} with email: {}", request.username(), request.email());
        
        // check duplicate username
        userRepository.findByUsername(request.username())
                .ifPresent(u -> {
                    log.warn("Username already exists: {}", request.username());
                    throw new UserAlreadyExistsException("Username '" + request.username() + "' already exists");
                });

        // check duplicate email - This was missing and caused 500 errors
        if (userRepository.existsByEmail(request.email())) {
            log.warn("Email already exists: {}", request.email());
            throw new UserAlreadyExistsException("Email '" + request.email() + "' already exists");
        }

        if (request.password() == null || request.password().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        
        // Default to USER role if not provided
        String role = (request.role() == null || request.role().isEmpty()) ? "USER" : request.role();
        user.setRole(role);
        
        user.setPassword(passwordEncoder.encode(request.password()));
        
        User savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());
        return savedUser;
    }
}
