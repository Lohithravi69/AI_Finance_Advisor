package com.aifa.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AlertRuleRequest(
    String ruleName,
    String ruleType,
    String condition,
    BigDecimal thresholdValue,
    String notificationType,
    String frequency
) {}
