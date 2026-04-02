package com.streamsphere.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DemoController {
    
    @GetMapping("/test")
    public String test() {
        return "Protected API Working";
    }
    
    @GetMapping("/user")
    public String user() {
        return "User API";
    }
    
    @GetMapping("/admin")
    public String admin() {
        return "Admin API";
    }
}
