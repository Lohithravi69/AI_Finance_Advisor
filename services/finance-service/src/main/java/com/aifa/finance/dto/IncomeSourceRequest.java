package com.aifa.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IncomeSourceRequest(
    String sourceName,
    String description,
    Long accountId,
    BigDecimal amount,
    String frequency,
    String incomeType,
    LocalDate startDate,
    LocalDate endDate
) {}
