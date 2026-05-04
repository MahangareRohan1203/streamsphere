package com.streamsphere.service;

import com.streamsphere.dto.SubscriptionRequest;
import com.streamsphere.dto.SubscriptionResponse;
import com.streamsphere.entity.Subscription;
import com.streamsphere.entity.SubscriptionStatus;
import com.streamsphere.entity.User;
import com.streamsphere.exception.UserNotFoundException;
import com.streamsphere.repository.SubscriptionRepository;
import com.streamsphere.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public SubscriptionResponse subscribe(Long userId, SubscriptionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User id '" + userId + "' not found"));

        // Mock payment processing...

        // Create new subscription (Valid for 30 days)
        Subscription subscription = Subscription.builder()
                .user(user)
                .tier(request.getTier())
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .build();

        Subscription saved = subscriptionRepository.save(subscription);

        // Update user's current tier if the new subscription is equal or higher weight
        if (user.getCurrentTier() == null || request.getTier().getWeight() >= user.getCurrentTier().getWeight()) {
            user.setCurrentTier(request.getTier());
            userRepository.save(user);
        }

        return mapToResponse(saved);
    }

    @Override
    public List<SubscriptionResponse> getUserSubscriptions(Long userId) {
        List<Subscription> subscriptions = subscriptionRepository.findByUserId(userId);
        return subscriptions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private SubscriptionResponse mapToResponse(Subscription subscription) {
        return SubscriptionResponse.builder()
                .id(subscription.getId())
                .tier(subscription.getTier())
                .status(subscription.getStatus())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .build();
    }
}
