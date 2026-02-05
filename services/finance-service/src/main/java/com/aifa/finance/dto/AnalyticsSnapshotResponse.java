package com.aifa.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AnalyticsSnapshotResponse(
    Long id,
    LocalDate snapshotDate,
    BigDecimal totalExpenses,
    BigDecimal totalIncome,
    BigDecimal netSavings,
    BigDecimal savingsRate,
    BigDecimal totalAssets,
    BigDecimal totalLiabilities,
    BigDecimal netWorth,
    BigDecimal liquidAssets,
    BigDecimal budgetUtilization,
    Integer overBudgetCount,
    String topCategory,
    BigDecimal topCategoryAmount,
    Integer transactionCount,
    BigDecimal averageTransaction,
    Integer activeGoalsCount,
    Integer completedGoalsCount,
    BigDecimal totalGoalProgress
) {}
