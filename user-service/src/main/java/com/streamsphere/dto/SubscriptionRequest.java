package com.streamsphere.dto;

import com.streamsphere.entity.SubscriptionTier;
import lombok.Data;

@Data
public class SubscriptionRequest {
    private SubscriptionTier tier;
}
