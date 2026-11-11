package com.github.Clementol.lovable_clone.controller;

import com.github.Clementol.lovable_clone.dto.subscription.PlanLimitResponse;
import com.github.Clementol.lovable_clone.dto.subscription.UsageTodayResponse;
import com.github.Clementol.lovable_clone.service.UsageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequestMapping("/api/usage")
public class UsageController {
    UsageService usageService;

    @GetMapping("/today")
    public ResponseEntity<UsageTodayResponse> getTodayUsage() {
        Long userId = 1L;
//        return ResponseEntity.ok(usageService.getTodayUsageOfUser(userId));
        return null;
    }

}
