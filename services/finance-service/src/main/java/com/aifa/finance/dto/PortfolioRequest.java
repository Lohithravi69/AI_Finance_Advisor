package com.aifa.finance.dto;

import java.math.BigDecimal;

public record PortfolioRequest(
    Long accountId,
    String portfolioName,
    String description,
    BigDecimal allocationStocks,
    BigDecimal allocationBonds,
    BigDecimal allocationCrypto,
    BigDecimal allocationOther
) {}
