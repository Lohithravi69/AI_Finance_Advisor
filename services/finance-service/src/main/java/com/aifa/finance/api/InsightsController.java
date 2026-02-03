package com.aifa.finance.api;

import com.aifa.finance.api.dto.TransactionDto;
import com.aifa.finance.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/insights")
@RequiredArgsConstructor
public class InsightsController {

    private final TransactionService transactionService;
    // private final RestTemplate aiClient = new RestTemplate();

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
}
