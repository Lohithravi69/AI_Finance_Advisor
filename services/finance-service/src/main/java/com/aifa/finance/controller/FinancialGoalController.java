package com.aifa.finance.controller;

import com.aifa.finance.dto.FinancialGoalRequest;
import com.aifa.finance.dto.FinancialGoalResponse;
import com.aifa.finance.service.FinancialGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class FinancialGoalController {

    private final FinancialGoalService goalService;

    @PostMapping
    public ResponseEntity<FinancialGoalResponse> createGoal(@RequestParam Long userId, @RequestBody FinancialGoalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(goalService.createGoal(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<FinancialGoalResponse>> getGoals(@RequestParam Long userId) {
        return ResponseEntity.ok(goalService.getGoalsByUser(userId));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<FinancialGoalResponse>> getUpcomingGoals(@RequestParam Long userId) {
        return ResponseEntity.ok(goalService.getUpcomingGoals(userId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<FinancialGoalResponse>> getGoalsByStatus(@RequestParam Long userId, @PathVariable String status) {
        return ResponseEntity.ok(goalService.getGoalsByStatus(userId, status));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<FinancialGoalResponse>> getGoalsByCategory(@RequestParam Long userId, @PathVariable String category) {
        return ResponseEntity.ok(goalService.getGoalsByCategory(userId, category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FinancialGoalResponse> getGoalById(@PathVariable Long id) {
        return ResponseEntity.ok(goalService.getGoalById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FinancialGoalResponse> updateGoal(@PathVariable Long id, @RequestBody FinancialGoalRequest request) {
        return ResponseEntity.ok(goalService.updateGoal(id, request));
    }

    @PutMapping("/{id}/progress")
    public ResponseEntity<FinancialGoalResponse> addProgress(@PathVariable Long id, @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(goalService.updateProgress(id, amount));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<FinancialGoalResponse> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(goalService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        goalService.deleteGoal(id);
        return ResponseEntity.noContent().build();
    }
}
