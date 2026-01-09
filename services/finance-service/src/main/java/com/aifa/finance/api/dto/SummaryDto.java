package com.aifa.finance.api.dto;

public record SummaryDto(
        String month,
        Double income,
        Double expenses,
        Double savings
) { }
