package com.aifa.finance.dto;

import java.math.BigDecimal;
import java.util.Map;

public record ExpenseSummaryReport(
    BigDecimal totalExpenses,
    BigDecimal averageExpense,
    Integer transactionCount,
    Map<String, BigDecimal> categoryBreakdown,
    Map<String, BigDecimal> dailyExpenses,
    BigDecimal highestExpense,
    String highestCategory
) {}
