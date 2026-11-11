package com.github.Clementol.lovable_clone.service;

import com.github.Clementol.lovable_clone.dto.subscription.PlanLimitResponse;
import com.github.Clementol.lovable_clone.dto.subscription.UsageTodayResponse;
import org.springframework.stereotype.Service;

@Service
public interface UsageService {
    void recordTokenUsage(Long userId, int actualTokens);
    void checkDailyTokenUsage();
}
