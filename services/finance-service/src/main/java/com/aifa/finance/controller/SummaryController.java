package com.aifa.finance.controller;

import com.aifa.finance.dto.FinancialSummaryResponse;
import com.aifa.finance.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/summary")
@RequiredArgsConstructor
public class SummaryController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<FinancialSummaryResponse> getMonthlySummary(
            @RequestParam(required = false) String month,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        String userId = extractUserIdFromToken(token);
        YearMonth yearMonth = month != null ? YearMonth.parse(month) : YearMonth.now();
        FinancialSummaryResponse summary = transactionService.getMonthlySummary(userId, yearMonth);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/yearly")
    public ResponseEntity<FinancialSummaryResponse> getYearlySummary(
            @RequestParam(required = false, defaultValue = "") String year,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        String userId = extractUserIdFromToken(token);
        FinancialSummaryResponse summary = transactionService.getYearlySummary(userId, year);
        return ResponseEntity.ok(summary);
    }

    private String extractUserIdFromToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return "test-user";
    }
}
