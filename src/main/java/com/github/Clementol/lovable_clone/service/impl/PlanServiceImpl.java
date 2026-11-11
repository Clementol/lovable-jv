package com.github.Clementol.lovable_clone.service.impl;

import com.github.Clementol.lovable_clone.dto.subscription.PlanResponse;
import com.github.Clementol.lovable_clone.service.PlanService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanServiceImpl implements PlanService {
    @Override
    public List<PlanResponse> getActivePlans() {
        return List.of();
    }
}
