package com.aifa.finance.repository;

import com.aifa.finance.domain.BudgetAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetAlertRepository extends JpaRepository<BudgetAlert, Long> {
    List<BudgetAlert> findByBudgetId(Long budgetId);
    List<BudgetAlert> findByBudgetIdOrderByTriggeredAtDesc(Long budgetId);
}
