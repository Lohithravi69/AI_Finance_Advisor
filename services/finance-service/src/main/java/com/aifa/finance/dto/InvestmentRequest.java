package com.aifa.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InvestmentRequest(
    Long accountId,
    String symbol,
    String name,
    String investmentType,
    BigDecimal quantity,
    BigDecimal purchasePrice,
    LocalDateTime purchaseDate,
    String currency,
    String notes
) {}
