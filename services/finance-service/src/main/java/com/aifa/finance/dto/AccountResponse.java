package com.aifa.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(
    Long id,
    String accountName,
    String accountType,
    String institutionName,
    String accountNumber,
    BigDecimal currentBalance,
    BigDecimal availableBalance,
    BigDecimal netBalance,
    String currency,
    String accountColor,
    Boolean isActive,
    Boolean isPrimary,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
