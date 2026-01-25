package com.aifa.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialSummaryResponse {
    private Double totalIncome;
    private Double totalExpenses;
    private Double netSavings;
    private Integer transactionCount;
    private String period;
}
