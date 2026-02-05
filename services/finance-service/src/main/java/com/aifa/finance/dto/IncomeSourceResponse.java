package com.aifa.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record IncomeSourceResponse(
    Long id,
    String sourceName,
    String description,
    Long accountId,
    BigDecimal amount,
    String frequency,
    String incomeType,
    BigDecimal annualAmount,
    BigDecimal monthlyAmount,
    LocalDate startDate,
    LocalDate endDate,
    LocalDate lastReceived,
    LocalDate nextExpected,
    Boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
