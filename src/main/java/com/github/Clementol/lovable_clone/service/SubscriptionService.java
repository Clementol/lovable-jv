package com.github.Clementol.lovable_clone.service;

import com.github.Clementol.lovable_clone.dto.subscription.*;
import com.github.Clementol.lovable_clone.enums.SubscriptionStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public interface SubscriptionService {
    SubscriptionResponse getCurrentSubscription();

    void activateSubscription(Long userId, Long planId, String subscriptionId, String customerId);

    void updateSubscription(String subId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId);

    void cancelSubscription(String subId);

    void renewSubscriptionPeriod(String subId, Instant periodStart, Instant periodEnd);

    void markSubscriptionPastDue(String subId);

    boolean canCreateNewProject();
}
