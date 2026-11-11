package com.github.Clementol.lovable_clone.service.impl;

import com.github.Clementol.lovable_clone.dto.subscription.PlanLimitResponse;
import com.github.Clementol.lovable_clone.dto.subscription.PlanResponse;
import com.github.Clementol.lovable_clone.dto.subscription.SubscriptionResponse;
import com.github.Clementol.lovable_clone.dto.subscription.UsageTodayResponse;
import com.github.Clementol.lovable_clone.entity.UsageLog;
import com.github.Clementol.lovable_clone.repository.UsageLogRepository;
import com.github.Clementol.lovable_clone.security.AuthUtil;
import com.github.Clementol.lovable_clone.service.SubscriptionService;
import com.github.Clementol.lovable_clone.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UsageServiceImpl implements UsageService {

    private final UsageLogRepository usageLogRepository;
    private final AuthUtil authUtil;
    private final SubscriptionService subscriptionService;

    @Override
    public void recordTokenUsage(Long userId, int actualTokens) {
        LocalDate today = LocalDate.now();

        UsageLog todayLog = usageLogRepository.findByUserIdAndDate(userId, today)
                .orElseGet(() -> createNewDailyLog(userId, today));
        todayLog.setTokensUsed(todayLog.getTokensUsed() + actualTokens);
        usageLogRepository.save(todayLog);
    }

    @Override
    public void checkDailyTokenUsage() {
        Long userId = authUtil.getCurrentUser();
        SubscriptionResponse subscriptionResponse = subscriptionService.getCurrentSubscription();
        PlanResponse planResponse = subscriptionResponse.plan();

        LocalDate today = LocalDate.now();

        UsageLog todayLog = usageLogRepository.findByUserIdAndDate(userId, today)
                .orElseGet(() -> createNewDailyLog(userId, today));
        if (planResponse.unlimitedAi()) return;

        int currentUsage = todayLog.getTokensUsed();
        int limit = planResponse.maxTokensPerDay();

        if (currentUsage >= limit) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Daily limit reached, Upgrade now"
                    );
        }
    }

    private UsageLog createNewDailyLog(Long userId, LocalDate date) {
        UsageLog newLog = UsageLog.builder()
                .userId(userId)
                .date(date)
                .tokensUsed(0)
                .build();
        return usageLogRepository.save(newLog);
    }
}
