package com.aifa.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CashFlowForecastResponse {
    private String forecastId;
    private String timestamp;
    private List<CashFlowPrediction> predictions;
    private ForecastMetadata metadata;
    private Double confidence;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CashFlowPrediction {
        private String date;
        private BigDecimal predictedBalance;
        private Double confidence;
        private String riskLevel;
        private List<String> factors;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ForecastMetadata {
        private Double modelAccuracy;
        private Integer predictionHorizon;
        private Integer dataPointsUsed;
        private String lastTransactionDate;
        private String error;
    }
}
