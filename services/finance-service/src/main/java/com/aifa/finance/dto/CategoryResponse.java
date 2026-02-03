package com.aifa.finance.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public record CategoryResponse(
    Long id,
    String name,
    String description,
    String icon,
    String color,
    Boolean isPredefined,
    Double monthlyBudget,
    Double spendingThisMonth,
    Double percentageSpent,
    Boolean isOverBudget,
    Boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) implements Serializable {}
