package com.aifa.finance.dto;

import java.io.Serializable;

public record ExpenseRuleRequest(
    Long categoryId,
    String pattern,
    String ruleType,
    String matchType,
    Integer priority
) implements Serializable {}
