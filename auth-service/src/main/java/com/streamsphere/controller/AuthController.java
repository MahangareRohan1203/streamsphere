package com.streamsphere.controller;

import com.streamsphere.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password) {
        
        // Dummy validation (we improve later)
        
        if ("admin".equals(username)) {
            return jwtUtil.generateToken(username, "ADMIN");
        }else if("user".equals(username))
            return jwtUtil.generateToken(username, "USER");
        
        throw new RuntimeException("Invalid credentials");
    }
    
}
