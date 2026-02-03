package com.aifa.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetResponse {
    private Long id;
    private String name;
    private String category;
    private BigDecimal monthlyLimit;
    private BigDecimal spentAmount;
    private Double percentageSpent;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer alertThreshold;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
