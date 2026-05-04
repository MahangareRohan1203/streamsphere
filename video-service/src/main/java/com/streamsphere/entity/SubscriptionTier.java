package com.streamsphere.entity;

public enum SubscriptionTier {
    FREE(0),
    PREMIUM(1),
    GOLD(2);

    private final int weight;

    SubscriptionTier(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}
