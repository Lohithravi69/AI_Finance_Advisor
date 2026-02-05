package com.aifa.finance.repository;

import com.aifa.finance.domain.AlertRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRuleRepository extends JpaRepository<AlertRule, Long> {

    List<AlertRule> findByUserIdOrderByRuleNameAsc(Long userId);

    List<AlertRule> findByUserIdAndIsEnabledTrue(Long userId);

    List<AlertRule> findByUserIdAndRuleType(Long userId, String ruleType);

    Long countByUserId(Long userId);

    void deleteByUserIdAndId(Long userId, Long ruleId);
}
