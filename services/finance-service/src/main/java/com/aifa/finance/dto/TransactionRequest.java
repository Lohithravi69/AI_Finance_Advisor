package com.aifa.finance.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    private String description;
    private BigDecimal amount;
    private String type; // INCOME, EXPENSE
    private String category;
    private String merchant;
    private LocalDateTime date;
}
