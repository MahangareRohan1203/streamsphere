package com.streamsphere.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
public class FallbackController {

    @RequestMapping("/fallback/auth")
    public Mono<Map<String, String>> authServiceFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Authentication service is currently unavailable. Please try again later.");
        response.put("status", "SERVICE_UNAVAILABLE");
        return Mono.just(response);
    }

    @RequestMapping("/fallback/user")
    public Mono<Map<String, String>> userServiceFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "User service is currently unavailable. Please try again later.");
        response.put("status", "SERVICE_UNAVAILABLE");
        return Mono.just(response);
    }

    @RequestMapping("/fallback/video")
    public Mono<Map<String, String>> videoServiceFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Video service is currently unavailable. Some features may be restricted.");
        response.put("status", "SERVICE_UNAVAILABLE");
        return Mono.just(response);
    }
}
