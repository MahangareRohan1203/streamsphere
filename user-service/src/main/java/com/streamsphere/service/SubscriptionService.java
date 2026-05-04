package com.streamsphere.service;

import com.streamsphere.dto.SubscriptionRequest;
import com.streamsphere.dto.SubscriptionResponse;

import java.util.List;

public interface SubscriptionService {
    SubscriptionResponse subscribe(Long userId, SubscriptionRequest request);
    List<SubscriptionResponse> getUserSubscriptions(Long userId);
}
