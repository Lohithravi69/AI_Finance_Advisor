package com.aifa.finance.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public record ExpenseRuleResponse(
    Long id,
    Long categoryId,
    String categoryName,
    String pattern,
    String ruleType,
    String matchType,
    Integer priority,
    Boolean isActive,
    Long matchCount,
    LocalDateTime createdAt
) implements Serializable {}
