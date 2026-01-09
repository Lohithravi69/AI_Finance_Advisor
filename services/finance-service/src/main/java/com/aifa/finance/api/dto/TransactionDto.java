package com.aifa.finance.api.dto;

import java.time.LocalDate;

public record TransactionDto(
        Long id,
        String description,
        Double amount,
        LocalDate date,
        String merchant,
        String category
) { }
