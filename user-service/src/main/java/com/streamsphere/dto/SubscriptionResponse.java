package com.streamsphere.dto;

import com.streamsphere.entity.SubscriptionStatus;
import com.streamsphere.entity.SubscriptionTier;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SubscriptionResponse {
    private Long id;
    private SubscriptionTier tier;
    private SubscriptionStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
