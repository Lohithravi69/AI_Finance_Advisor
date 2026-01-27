package com.aifa.finance.api.dto;

import java.time.LocalDate;

public record TransactionDto(
        Long id,
        String type,
        String description,
        Double amount,
        LocalDate date,
        String merchant,
        String category
) { }
