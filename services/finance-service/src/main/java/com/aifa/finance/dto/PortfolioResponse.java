package com.aifa.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PortfolioResponse(
    Long id,
    String portfolioName,
    String description,
    BigDecimal totalInvested,
    BigDecimal currentValue,
    BigDecimal totalGainLoss,
    BigDecimal totalReturnPercentage,
    Integer investmentCount,
    BigDecimal allocationStocks,
    BigDecimal allocationBonds,
    BigDecimal allocationCrypto,
    BigDecimal allocationOther,
    Boolean rebalanceNeeded,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
