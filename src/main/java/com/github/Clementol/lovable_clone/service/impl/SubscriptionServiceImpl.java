package com.github.Clementol.lovable_clone.service.impl;


import com.github.Clementol.lovable_clone.dto.subscription.SubscriptionResponse;
import com.github.Clementol.lovable_clone.entity.Plan;
import com.github.Clementol.lovable_clone.entity.Subscription;
import com.github.Clementol.lovable_clone.entity.User;
import com.github.Clementol.lovable_clone.enums.SubscriptionStatus;
import com.github.Clementol.lovable_clone.error.ResourceNotFoundException;
import com.github.Clementol.lovable_clone.mapper.SubscriptionMapper;
import com.github.Clementol.lovable_clone.repository.PlanRepository;
import com.github.Clementol.lovable_clone.repository.ProjectMemberRepository;
import com.github.Clementol.lovable_clone.repository.SubscriptionRepository;
import com.github.Clementol.lovable_clone.repository.UserRepository;
import com.github.Clementol.lovable_clone.security.AuthUtil;
import com.github.Clementol.lovable_clone.service.SubscriptionService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final AuthUtil authUtil;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final ProjectMemberRepository projectMemberRepository;

    private final Integer FREE_TIER_PROJECTS_ALLOWED = 100;

    @Override
    public SubscriptionResponse getCurrentSubscription() {
        Long userId = authUtil.getCurrentUser();
        var currentSubscription = subscriptionRepository.findByUserIdAndStatusIn(userId, Set.of(
                SubscriptionStatus.ACTIVE, SubscriptionStatus.PAST_DUE,
                SubscriptionStatus.TRIALING
        )).orElse(null);

        return subscriptionMapper.toSubscriptionResponse(currentSubscription);
    }

    @Override
    public void activateSubscription(Long userId, Long planId, String subscriptionId, String customerId) {

        boolean exists = subscriptionRepository.existsByStripeSubscriptionId(subscriptionId);
        if (exists) return;

        User user = getUser(userId);
        Plan plan = getPlan(planId);

        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .stripeSubscriptionId(subscriptionId)
                .status(SubscriptionStatus.INCOMPLETE)
                .build();

        subscriptionRepository.save(subscription);

    }

    @Override
    @Transactional
    public void updateSubscription(String subId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId) {
        Subscription subscription = this.getSubscription(subId);
        boolean hasSubscriptionUpdated = false;
        if (status != null && status != subscription.getStatus()) {
            subscription.setStatus(status);
            hasSubscriptionUpdated = true;
        }

        if (periodStart != null && !periodStart.equals(subscription.getCurrentPeriodStart())) {
            subscription.setCurrentPeriodStart(subscription.getCurrentPeriodStart());
            hasSubscriptionUpdated = true;
        }

        if (periodEnd != null && !periodEnd.equals(subscription.getCurrentPeriodEnd())) {
            subscription.setCurrentPeriodEnd(periodEnd);
            hasSubscriptionUpdated = true;
        }

        if (cancelAtPeriodEnd != null && !cancelAtPeriodEnd.equals(subscription.getCancelledAtPeriodEnd())) {
            subscription.setCancelledAtPeriodEnd(cancelAtPeriodEnd);
            hasSubscriptionUpdated = true;
        }

        if (planId != null && !planId.equals(subscription.getPlan().getId())) {
            Plan newPlan = getPlan(planId);
            subscription.setPlan(newPlan);
            hasSubscriptionUpdated = true;
        }
        if (hasSubscriptionUpdated) {
            subscriptionRepository.save(subscription);
            log.debug("Subscription has been updated: {}", subId);
        }
    }

    @Override
    public void cancelSubscription(String gatewaySubscriptionId) {
        Subscription subscription = this.getSubscription(gatewaySubscriptionId);
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscriptionRepository.save(subscription);
    }

    @Override
    public void renewSubscriptionPeriod(String subId, Instant periodStart, Instant periodEnd) {
        Subscription subscription = getSubscription(subId);

        Instant newStart = periodStart != null ? periodStart : subscription.getCurrentPeriodEnd();
        subscription.setCurrentPeriodStart(newStart);
        subscription.setCurrentPeriodEnd(periodEnd);

        if (subscription.getStatus() == SubscriptionStatus.PAST_DUE || subscription.getStatus() == SubscriptionStatus.INCOMPLETE) {
            subscription.setStatus(SubscriptionStatus.ACTIVE);
        }
        subscriptionRepository.save(subscription);
    }



    @Override
    public void markSubscriptionPastDue(String subId) {
        Subscription subscription = this.getSubscription(subId);
        if (subscription.getStatus() == SubscriptionStatus.PAST_DUE) {
            log.debug("Subscription is already past due, subId: {}", subId);
            return;
        }
        subscription.setStatus(SubscriptionStatus.PAST_DUE);
        subscriptionRepository.save(subscription);

        // Notify user via email
    }

    @Override
    public boolean canCreateNewProject() {
        Long userId = authUtil.getCurrentUser();
        SubscriptionResponse currentSubscription = this.getCurrentSubscription();

        int countProjectOwnedByUser = projectMemberRepository.countProjectOwnedByUser(userId);

        if (currentSubscription == null || currentSubscription.plan() == null) {
            return countProjectOwnedByUser < FREE_TIER_PROJECTS_ALLOWED;
        }
        return countProjectOwnedByUser <= currentSubscription.plan().maxProjects();
    }

    // utility methods
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));
    }

    private Plan getPlan(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan", planId.toString()));
    }

    private Subscription getSubscription(String subId) {
        return subscriptionRepository.findByStripeSubscriptionId(subId).orElseThrow(
                () -> new ResourceNotFoundException("Subscription", subId)
        );
    }
}
