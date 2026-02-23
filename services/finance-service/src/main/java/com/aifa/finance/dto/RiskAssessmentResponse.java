package com.aifa.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskAssessmentResponse {
    private String assessmentId;
    private String timestamp;
    private RiskAssessment riskAssessment;
    private List<InsightResponse> insights;
    private Double confidence;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RiskAssessment {
        private Double overallRiskScore;
        private List<String> riskFactors;
        private List<String> mitigationStrategies;
        private Double confidenceLevel;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
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
