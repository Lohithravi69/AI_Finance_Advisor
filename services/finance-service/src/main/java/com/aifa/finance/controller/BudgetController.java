package com.aifa.finance.controller;

import com.aifa.finance.dto.BudgetRequest;
import com.aifa.finance.dto.BudgetResponse;
import com.aifa.finance.dto.BudgetAlertResponse;
import com.aifa.finance.service.BudgetService;
import com.aifa.finance.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {
    private final BudgetService budgetService;
    private final AuthService authService;

    /**
     * Create a new budget
     */
    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody BudgetRequest request) {
        Long userId = authService.getOrCreateUser(jwt).getId();
        BudgetResponse response = budgetService.createBudget(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all budgets for current user
     */
    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getBudgets(@AuthenticationPrincipal Jwt jwt) {
        Long userId = authService.getOrCreateUser(jwt).getId();
        List<BudgetResponse> budgets = budgetService.getBudgetsByUser(userId);
        return ResponseEntity.ok(budgets);
    }

    /**
     * Get active budgets for a specific date
     */
    @GetMapping("/active")
    public ResponseEntity<List<BudgetResponse>> getActiveBudgets(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) LocalDate date) {
        Long userId = authService.getOrCreateUser(jwt).getId();
        LocalDate queryDate = date != null ? date : LocalDate.now();
        List<BudgetResponse> budgets = budgetService.getActiveBudgets(userId, queryDate);
        return ResponseEntity.ok(budgets);
    }

    /**
     * Get a specific budget
     */
    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponse> getBudget(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = authService.getOrCreateUser(jwt).getId();
        BudgetResponse budget = budgetService.getBudget(id, userId);
        return ResponseEntity.ok(budget);
    }

    /**
     * Get budget status
     */
    @GetMapping("/{id}/status")
    public ResponseEntity<BudgetResponse> getBudgetStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = authService.getOrCreateUser(jwt).getId();
        BudgetResponse status = budgetService.getBudgetStatus(id, userId);
        return ResponseEntity.ok(status);
    }

    /**
     * Update a budget
     */
    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponse> updateBudget(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody BudgetRequest request) {
        Long userId = authService.getOrCreateUser(jwt).getId();
        BudgetResponse response = budgetService.updateBudget(id, userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a budget
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = authService.getOrCreateUser(jwt).getId();
        budgetService.deleteBudget(id, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get budget alerts
     */
    @GetMapping("/{id}/alerts")
    public ResponseEntity<List<BudgetAlertResponse>> getBudgetAlerts(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = authService.getOrCreateUser(jwt).getId();
        List<BudgetAlertResponse> alerts = budgetService.getBudgetAlerts(id, userId);
        return ResponseEntity.ok(alerts);
    }
}
