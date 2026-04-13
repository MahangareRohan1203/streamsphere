package com.streamsphere.service;

import com.streamsphere.dto.CreateUserRequest;
import com.streamsphere.entity.User;

public interface UserService {
    User getUserByUsername(String username);
    User createUser(CreateUserRequest request);
}
