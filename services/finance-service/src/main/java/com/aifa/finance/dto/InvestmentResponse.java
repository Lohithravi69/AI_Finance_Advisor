package com.aifa.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InvestmentResponse(
    Long id,
    String symbol,
    String name,
    String investmentType,
    BigDecimal quantity,
    BigDecimal purchasePrice,
    BigDecimal currentPrice,
    BigDecimal totalCost,
    BigDecimal currentValue,
    BigDecimal gainLoss,
    BigDecimal gainLossPercentage,
    LocalDateTime purchaseDate,
    LocalDateTime lastUpdated,
    String currency,
    String notes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
