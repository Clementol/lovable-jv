package com.github.Clementol.lovable_clone.repository;

import com.github.Clementol.lovable_clone.entity.Subscription;
import com.github.Clementol.lovable_clone.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

  /**
   * Get the current active subscripton
   * */
  Optional<Subscription> findByUserIdAndStatusIn(Long userId, Set<SubscriptionStatus> statusSet);

  boolean existsByStripeSubscriptionId(String subscriptionId);

  Optional<Subscription> findByStripeSubscriptionId(String subId);
}