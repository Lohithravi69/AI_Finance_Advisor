package com.aifa.finance.dto;

import java.math.BigDecimal;

public record AccountRequest(
    String accountName,
    String accountType,
    String institutionName,
    String accountNumber,
    BigDecimal currentBalance,
    BigDecimal availableBalance,
    String currency,
    String accountColor,
    Boolean isPrimary
) {}
