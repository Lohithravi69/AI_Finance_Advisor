package com.aifa.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record FinancialGoalResponse(
    Long id,
    String goalName,
    String goalCategory,
    String description,
    BigDecimal targetAmount,
    BigDecimal currentAmount,
    BigDecimal progressPercentage,
    LocalDate startDate,
    LocalDate targetDate,
    String status,
    String priority,
    BigDecimal monthlyContribution,
    Long accountId,
    Boolean isRecurring,
    LocalDate completionDate,
    Long daysRemaining,
    String notes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
