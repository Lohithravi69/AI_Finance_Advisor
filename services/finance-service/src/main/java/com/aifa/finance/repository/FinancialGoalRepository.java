package com.aifa.finance.repository;

import com.aifa.finance.domain.FinancialGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface FinancialGoalRepository extends JpaRepository<FinancialGoal, Long> {

    List<FinancialGoal> findByUserIdOrderByTargetDateAsc(Long userId);

    List<FinancialGoal> findByUserIdAndStatus(Long userId, String status);

    List<FinancialGoal> findByUserIdAndGoalCategory(Long userId, String goalCategory);

    List<FinancialGoal> findByUserIdAndPriority(Long userId, String priority);

    @Query("SELECT g FROM FinancialGoal g WHERE g.user.id = :userId AND g.targetDate >= :today ORDER BY g.targetDate ASC")
    List<FinancialGoal> findUpcomingGoals(Long userId, LocalDate today);

    @Query("SELECT COUNT(g) FROM FinancialGoal g WHERE g.user.id = :userId AND g.status = 'COMPLETED'")
    Long countCompletedGoals(Long userId);

    @Query("SELECT SUM(g.targetAmount) FROM FinancialGoal g WHERE g.user.id = :userId AND g.status != 'CANCELLED'")
    BigDecimal sumTargetAmounts(Long userId);

    @Query("SELECT SUM(g.currentAmount) FROM FinancialGoal g WHERE g.user.id = :userId AND g.status != 'CANCELLED'")
    BigDecimal sumCurrentAmounts(Long userId);

    @Query("SELECT AVG(g.progressPercentage) FROM FinancialGoal g WHERE g.user.id = :userId AND g.status = 'IN_PROGRESS'")
    BigDecimal getAverageProgress(Long userId);
}
