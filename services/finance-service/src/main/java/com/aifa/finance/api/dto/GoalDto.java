package com.aifa.finance.api.dto;

public record GoalDto(
    Long id,
    String name,
    double targetAmount,
    double currentAmount,
    String status
) {}
