package com.aifa.finance.dto;

import java.math.BigDecimal;

public record AlertRuleRequest(
    String ruleName,
    String ruleType,
    String condition,
    BigDecimal thresholdValue,
    String notificationType,
    String frequency
) {}
