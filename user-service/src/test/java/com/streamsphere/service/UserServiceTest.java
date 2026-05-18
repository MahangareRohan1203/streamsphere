package com.streamsphere.service;

import com.streamsphere.dto.CreateUserRequest;
import com.streamsphere.entity.User;
import com.streamsphere.exception.UserAlreadyExistsException;
import com.streamsphere.exception.UserNotFoundException;
import com.streamsphere.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUserByUsername_UserExists() {
        User user = new User();
        user.setUsername("user1");
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        User result = userService.getUserByUsername("user1");

        assertNotNull(result);
        assertEquals("user1", result.getUsername());
    }

    @Test
    void getUserByUsername_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserByUsername("nonexistent"));
    }

    @Test
    void createUser_Success() {
        CreateUserRequest request = new CreateUserRequest("user1", "password", "user1@example.com", "USER");
        when(userRepository.findByUsername("user1")).thenReturn(Optional.empty());
        when(userRepository.existsByEmail("user1@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.createUser(request);

        assertNotNull(result);
        assertEquals("user1", result.getUsername());
        assertEquals("user1@example.com", result.getEmail());
        assertEquals("encoded_password", result.getPassword());
    }

    @Test
    void createUser_DuplicateUsername_ThrowsException() {
        CreateUserRequest request = new CreateUserRequest("user1", "password", "user1@example.com", "USER");
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_DuplicateEmail_ThrowsException() {
        CreateUserRequest request = new CreateUserRequest("user1", "password", "user1@example.com", "USER");
        when(userRepository.findByUsername("user1")).thenReturn(Optional.empty());
        when(userRepository.existsByEmail("user1@example.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_NullPassword_ThrowsException() {
        CreateUserRequest request = new CreateUserRequest("user1", null, "user1@example.com", "USER");
        when(userRepository.findByUsername("user1")).thenReturn(Optional.empty());
        when(userRepository.existsByEmail("user1@example.com")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(request));
    }
}
