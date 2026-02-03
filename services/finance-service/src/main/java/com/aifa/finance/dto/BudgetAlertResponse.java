package com.aifa.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetAlertResponse {
    private Long id;
    private Long budgetId;
    private String alertType;
    private Integer percentage;
    private LocalDateTime triggeredAt;
}
