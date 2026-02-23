package com.aifa.finance.controller;

import com.aifa.finance.dto.FinancialSummaryResponse;
import com.aifa.finance.domain.User;
import com.aifa.finance.service.AuthService;
import com.aifa.finance.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/summary")
@RequiredArgsConstructor
public class SummaryController {

    private final AuthService authService;
    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<FinancialSummaryResponse> getMonthlySummary(
            @RequestParam(required = false) String month,
            @AuthenticationPrincipal Jwt jwt) {

        User user = authService.getOrCreateUser(jwt);
        YearMonth yearMonth = month != null ? YearMonth.parse(month) : YearMonth.now();
        FinancialSummaryResponse summary = transactionService.getMonthlySummary(user.getKeycloakId(), yearMonth);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/yearly")
    public ResponseEntity<FinancialSummaryResponse> getYearlySummary(
            @RequestParam(required = false, defaultValue = "") String year,
            @AuthenticationPrincipal Jwt jwt) {

        User user = authService.getOrCreateUser(jwt);
        FinancialSummaryResponse summary = transactionService.getYearlySummary(user.getKeycloakId(), year);
        return ResponseEntity.ok(summary);
    }
}
