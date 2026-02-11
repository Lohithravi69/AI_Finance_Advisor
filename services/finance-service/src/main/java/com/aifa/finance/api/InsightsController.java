package com.aifa.finance.api;

import com.aifa.finance.api.dto.TransactionDto;
import com.aifa.finance.dto.CashFlowForecastResponse;
import com.aifa.finance.dto.GoalPredictionResponse;
import com.aifa.finance.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/insights")
@RequiredArgsConstructor
public class InsightsController {

    private final TransactionService transactionService;
    private final RestTemplate aiClient = new RestTemplate();

    @Value("${ai.service.url:http://localhost:8000}")
    private String aiServiceUrl;

    @GetMapping
    public List<Map<String, String>> insights(@AuthenticationPrincipal Jwt jwt) {
        // Get user transactions
        var transactions = transactionService.listTransactions(jwt, 50);  // last 50 transactions

        // Call AI for categorization if needed, but for now, analyze locally
        // For real AI insights, we could call /advise or custom endpoint

        // Simple analysis: calculate category spending
        Map<String, Double> categorySpend = transactions.stream()
            .filter(t -> t.category() != null)
            .collect(Collectors.groupingBy(
                TransactionDto::category,
                Collectors.summingDouble(t -> t.amount() != null ? t.amount() : 0.0)
            ));

        // Call AI for advice - commented out for now
        /*
        try {
            var response = aiClient.postForObject(
                "http://localhost:8001/advise",
                Map.of(
                    "income", 5000.0,  // hardcoded for demo
                    "fixed_expenses", 1200.0,
                    "variable_expenses", 1600.0,
                    "savings_goal", 10000.0
                ),
                Map.class
            );

            if (response != null) {
                return List.of(
                    Map.of(
                        "type", "saving",
                        "message", "AI suggests saving $" + response.get("suggested_savings") + " monthly. " + response.get("note")
                    )
                );
            }
        } catch (Exception e) {
            // fallback
        }
        */

        // Generate insights based on category spending
        List<Map<String, String>> insights = new java.util.ArrayList<>();

        if (!categorySpend.isEmpty()) {
            // Find top spending category
            var topCategory = categorySpend.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

            if (topCategory != null) {
                insights.add(Map.of(
                    "type", "spending",
                    "message", "Your highest spending category is " + topCategory.getKey() + " with $" + String.format("%.2f", topCategory.getValue()) + "."
                ));
            }

            // Calculate total expenses
            double totalExpenses = categorySpend.values().stream().mapToDouble(Double::doubleValue).sum();
            insights.add(Map.of(
                "type", "summary",
                "message", "Total expenses across categories: $" + String.format("%.2f", totalExpenses) + "."
            ));
        } else {
            // Fallback if no categorized expenses
            insights.add(Map.of(
                "type", "overspend",
                "message", "You overspent on dining by 18% this month."
            ));
            insights.add(Map.of(
                "type", "saving",
                "message", "Consider automating a $200 transfer on payday."
            ));
        }

        return insights;
    }

    /**
     * Get cash flow forecast
     */
    @GetMapping("/cashflow-forecast")
    public CashFlowForecastResponse getCashFlowForecast(@AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getSubject() != null ? Long.parseLong(jwt.getSubject()) : 1L;

        // Get user transactions for forecasting
        var transactions = transactionService.listTransactions(jwt, 100); // Get more data for better forecasting

        // Convert to format expected by AI service
        List<Map<String, Object>> transactionData = transactions.stream()
            .map(t -> Map.<String, Object>of(
                "id", t.id(),
                "type", t.type(),
                "amount", t.amount(),
                "date", t.date(),
                "category", t.category()
            ))
            .collect(Collectors.toList());

        try {
            // Call AI service for cash flow prediction
            var response = aiClient.postForObject(
                aiServiceUrl + "/ai/predict/cashflow",
                Map.of("transactions", transactionData, "monthsAhead", 6),
                Map.class
            );

            if (response != null) {
                // Convert AI response to our DTO
                List<CashFlowForecastResponse.CashFlowPrediction> predictions =
                    ((List<Map<String, Object>>) response.get("predictions")).stream()
                        .map(pred -> CashFlowForecastResponse.CashFlowPrediction.builder()
                            .date((String) pred.get("date"))
                            .predictedBalance(BigDecimal.valueOf(((Number) pred.get("predicted_balance")).doubleValue()))
                            .confidence(((Number) pred.get("confidence")).doubleValue())
                            .riskLevel((String) pred.get("risk_level"))
                            .factors((List<String>) pred.get("factors"))
                            .build())
                        .collect(Collectors.toList());

                Map<String, Object> metadata = (Map<String, Object>) response.get("metadata");

                return CashFlowForecastResponse.builder()
                    .forecastId((String) response.get("prediction_id"))
                    .timestamp((String) response.get("timestamp"))
                    .predictions(predictions)
                    .metadata(CashFlowForecastResponse.ForecastMetadata.builder()
                        .modelAccuracy(((Number) metadata.get("model_accuracy")).doubleValue())
                        .predictionHorizon(((Number) metadata.get("prediction_horizon")).intValue())
                        .dataPointsUsed(((Number) metadata.get("data_points_used")).intValue())
                        .lastTransactionDate((String) metadata.get("last_transaction_date"))
                        .error((String) metadata.get("error"))
                        .build())
                    .confidence(((Number) response.get("confidence")).doubleValue())
                    .build();
            }
        } catch (Exception e) {
            // Log error and return fallback
            System.err.println("Cash flow forecast failed: " + e.getMessage());
        }

        // Fallback response
        return CashFlowForecastResponse.builder()
            .forecastId("fallback_" + System.currentTimeMillis())
            .timestamp(java.time.Instant.now().toString())
            .predictions(List.of())
            .metadata(CashFlowForecastResponse.ForecastMetadata.builder()
                .modelAccuracy(0.5)
                .predictionHorizon(6)
                .dataPointsUsed(0)
                .error("AI service unavailable")
                .build())
            .confidence(0.1)
            .build();
    }

    /**
     * Get goal achievement prediction
     */
    @PostMapping("/goal-prediction")
    public GoalPredictionResponse getGoalPrediction(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Map<String, Object> request) {

        Double currentSavings = ((Number) request.get("currentSavings")).doubleValue();
        Double monthlyContribution = ((Number) request.get("monthlyContribution")).doubleValue();
        Double targetAmount = ((Number) request.get("targetAmount")).doubleValue();
        Double expectedReturnRate = ((Number) request.getOrDefault("expectedReturnRate", 0.07)).doubleValue();

        try {
            // Call AI service for goal prediction
            var response = aiClient.postForObject(
                aiServiceUrl + "/ai/predict/goals",
                Map.of(
                    "currentSavings", currentSavings,
                    "monthlyContribution", monthlyContribution,
                    "targetAmount", targetAmount,
                    "expectedReturnRate", expectedReturnRate
                ),
                Map.class
            );

            if (response != null) {
                // Convert AI response to our DTO
                List<GoalPredictionResponse.InsightResponse> insights =
                    ((List<Map<String, Object>>) response.get("insights")).stream()
                        .map(insight -> GoalPredictionResponse.InsightResponse.builder()
                            .type((String) insight.get("type"))
                            .severity((String) insight.get("severity"))
                            .category((String) insight.get("category"))
                            .message((String) insight.get("message"))
                            .currentAmount(((Number) insight.get("current_amount")).doubleValue())
                            .targetAmount(insight.get("target_amount") != null ?
                                ((Number) insight.get("target_amount")).doubleValue() : null)
                            .percentageChange(insight.get("percentage_change") != null ?
                                ((Number) insight.get("percentage_change")).doubleValue() : null)
                            .build())
                        .collect(Collectors.toList());

                return GoalPredictionResponse.builder()
                    .forecastId((String) response.get("forecast_id"))
                    .timestamp((String) response.get("timestamp"))
                    .forecast((Map<String, Object>) response.get("forecast"))
                    .insights(insights)
                    .confidence(((Number) response.get("confidence")).doubleValue())
                    .build();
            }
        } catch (Exception e) {
            // Log error and return fallback
            System.err.println("Goal prediction failed: " + e.getMessage());
        }

        // Fallback response
        return GoalPredictionResponse.builder()
            .forecastId("fallback_" + System.currentTimeMillis())
            .timestamp(java.time.Instant.now().toString())
            .forecast(Map.of("error", "AI service unavailable"))
            .insights(List.of())
            .confidence(0.1)
            .build();
    }
}
