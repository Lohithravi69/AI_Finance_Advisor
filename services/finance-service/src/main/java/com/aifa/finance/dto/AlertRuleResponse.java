package com.aifa.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AlertRuleResponse(
    Long id,
    String ruleName,
    String ruleType,
    String condition,
    BigDecimal thresholdValue,
    String notificationType,
    Boolean isEnabled,
    String frequency,
    LocalDateTime lastTriggered,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
