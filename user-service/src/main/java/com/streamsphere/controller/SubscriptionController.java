package com.streamsphere.controller;

import com.streamsphere.dto.SubscriptionRequest;
import com.streamsphere.dto.SubscriptionResponse;
import com.streamsphere.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/subscriptions")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @PostMapping
    public SubscriptionResponse subscribe(@PathVariable Long userId, @RequestBody SubscriptionRequest request) {
        return subscriptionService.subscribe(userId, request);
    }

    @GetMapping
    public List<SubscriptionResponse> getUserSubscriptions(@PathVariable Long userId) {
        return subscriptionService.getUserSubscriptions(userId);
    }
}
