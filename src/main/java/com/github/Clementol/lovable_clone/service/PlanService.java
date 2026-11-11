package com.github.Clementol.lovable_clone.service;

import com.github.Clementol.lovable_clone.dto.subscription.PlanResponse;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PlanService {

    List<PlanResponse> getActivePlans();
}
