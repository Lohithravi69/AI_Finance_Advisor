package com.aifa.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FinancialGoalRequest(
    String goalName,
    String goalCategory,
    String description,
    BigDecimal targetAmount,
    LocalDate startDate,
    LocalDate targetDate,
    String priority,
    BigDecimal monthlyContribution,
    Long accountId,
    Boolean isRecurring,
    String notes
) {}
