package com.aifa.finance.dto;

import java.io.Serializable;

public record CategorySpendingAnalysisResponse(
    Long categoryId,
    String categoryName,
    String icon,
    String color,
    Double totalSpent,
    Double monthlyBudget,
    Double percentageOfTotal,
    Double percentageOfBudget,
    Integer transactionCount,
    Double averageTransaction,
    String trendIndicator, // "UP", "DOWN", "STABLE"
    Boolean isAnomalous
) implements Serializable {}
