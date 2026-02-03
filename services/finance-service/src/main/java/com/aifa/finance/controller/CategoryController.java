package com.aifa.finance.controller;

import com.aifa.finance.dto.*;
import com.aifa.finance.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST API Controller for category management and expense categorization.
 * Endpoints for CRUD operations, rules, and spending analysis.
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {
    
    private final CategoryService categoryService;

    /**
     * Create a new category
     * POST /api/categories
     */
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CategoryRequest request) {
        Long userId = extractUserId(jwt);
        log.info("Creating category for user {}: {}", userId, request.name());
        
        CategoryResponse response = categoryService.createCategory(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all categories for the authenticated user
     * GET /api/categories
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories(
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = extractUserId(jwt);
        log.info("Fetching categories for user {}", userId);
        
        List<CategoryResponse> categories = categoryService.getCategoriesByUser(userId);
        return ResponseEntity.ok(categories);
    }

    /**
     * Get only active categories
     * GET /api/categories/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<CategoryResponse>> getActiveCategories(
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = extractUserId(jwt);
        
        List<CategoryResponse> categories = categoryService.getActiveCategoriesByUser(userId);
        return ResponseEntity.ok(categories);
    }

    /**
     * Get a specific category
     * GET /api/categories/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {
        Long userId = extractUserId(jwt);
        log.info("Fetching category {} for user {}", id, userId);
        
        CategoryResponse category = categoryService.getCategoryById(userId, id);
        return ResponseEntity.ok(category);
    }

    /**
     * Update a category
     * PUT /api/categories/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            @RequestBody CategoryRequest request) {
        Long userId = extractUserId(jwt);
        log.info("Updating category {} for user {}", id, userId);
        
        CategoryResponse response = categoryService.updateCategory(userId, id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a category
     * DELETE /api/categories/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {
        Long userId = extractUserId(jwt);
        log.info("Deleting category {} for user {}", id, userId);
        
        categoryService.deleteCategory(userId, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Auto-categorize a transaction
     * POST /api/categories/auto-categorize
     */
    @PostMapping("/auto-categorize")
    public ResponseEntity<Map<String, String>> autoCategorizeTransaction(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam String description,
            @RequestParam(required = false) String merchant) {
        Long userId = extractUserId(jwt);
        log.info("Auto-categorizing transaction for user {}", userId);
        
        String category = categoryService.autoCategorizeTransaction(userId, description, merchant);
        return ResponseEntity.ok(Map.of("category", category));
    }

    /**
     * Analyze spending by category with anomaly detection
     * GET /api/categories/analysis/spending
     */
    @GetMapping("/analysis/spending")
    public ResponseEntity<List<CategorySpendingAnalysisResponse>> analyzeSpending(
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = extractUserId(jwt);
        log.info("Analyzing spending for user {}", userId);
        
        List<CategorySpendingAnalysisResponse> analysis = categoryService.analyzeSpendingByCategory(userId);
        return ResponseEntity.ok(analysis);
    }

    /**
     * Get spending breakdown by category (for charts)
     * GET /api/categories/breakdown
     */
    @GetMapping("/breakdown")
    public ResponseEntity<Map<String, Double>> getSpendingBreakdown(
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = extractUserId(jwt);
        log.info("Fetching spending breakdown for user {}", userId);
        
        Map<String, Double> breakdown = categoryService.getSpendingBreakdown(userId);
        return ResponseEntity.ok(breakdown);
    }

    /**
     * Get categories over budget
     * GET /api/categories/over-budget
     */
    @GetMapping("/over-budget")
    public ResponseEntity<List<CategoryResponse>> getOverBudgetCategories(
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = extractUserId(jwt);
        log.info("Fetching over-budget categories for user {}", userId);
        
        List<CategoryResponse> overBudget = categoryService.getOverBudgetCategories(userId);
        return ResponseEntity.ok(overBudget);
    }

    // ============ EXPENSE RULES ENDPOINTS ============

    /**
     * Create an expense categorization rule
     * POST /api/categories/rules
     */
    @PostMapping("/rules")
    public ResponseEntity<ExpenseRuleResponse> createRule(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody ExpenseRuleRequest request) {
        Long userId = extractUserId(jwt);
        log.info("Creating expense rule for user {}: {}", userId, request.pattern());
        
        ExpenseRuleResponse response = categoryService.createRule(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all rules for the user
     * GET /api/categories/rules
     */
    @GetMapping("/rules")
    public ResponseEntity<List<ExpenseRuleResponse>> getRules(
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = extractUserId(jwt);
        log.info("Fetching rules for user {}", userId);
        
        List<ExpenseRuleResponse> rules = categoryService.getRulesByUser(userId);
        return ResponseEntity.ok(rules);
    }

    /**
     * Delete an expense rule
     * DELETE /api/categories/rules/{ruleId}
     */
    @DeleteMapping("/rules/{ruleId}")
    public ResponseEntity<Void> deleteRule(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long ruleId) {
        Long userId = extractUserId(jwt);
        log.info("Deleting rule {} for user {}", ruleId, userId);
        
        categoryService.deleteRule(userId, ruleId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Extract user ID from JWT token
     */
    private Long extractUserId(Jwt jwt) {
        String userIdClaim = jwt.getClaimAsString("user_id");
        if (userIdClaim == null) {
            userIdClaim = jwt.getClaimAsString("sub");
        }
        return Long.parseLong(userIdClaim);
    }
}
