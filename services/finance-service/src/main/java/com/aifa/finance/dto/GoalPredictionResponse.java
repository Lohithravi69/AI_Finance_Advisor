package com.aifa.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalPredictionResponse {
    private String forecastId;
    private String timestamp;
    private Map<String, Object> forecast;
    private List<InsightResponse> insights;
    private Double confidence;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InsightResponse {
        private String type;
        private String severity;
        private String category;
        private String message;
        private Double currentAmount;
        private Double targetAmount;
        private Double percentageChange;
    }
}
