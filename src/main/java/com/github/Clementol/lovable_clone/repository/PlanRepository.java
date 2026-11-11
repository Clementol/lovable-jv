package com.github.Clementol.lovable_clone.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.github.Clementol.lovable_clone.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    Optional<Plan> findByStripePriceId(String id);
}