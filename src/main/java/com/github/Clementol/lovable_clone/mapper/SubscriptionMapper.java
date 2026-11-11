package com.github.Clementol.lovable_clone.mapper;

import com.github.Clementol.lovable_clone.dto.subscription.PlanResponse;
import com.github.Clementol.lovable_clone.dto.subscription.SubscriptionResponse;
import com.github.Clementol.lovable_clone.entity.Plan;
import com.github.Clementol.lovable_clone.entity.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    @Mapping(target = "status", expression = "java(subscription.getStatus() != null ? subscription.getStatus().name() : null)")
    @Mapping(target = "plan", source = "plan")
    SubscriptionResponse toSubscriptionResponse(Subscription subscription);

    PlanResponse toPlanResponse(Plan plan);
}
