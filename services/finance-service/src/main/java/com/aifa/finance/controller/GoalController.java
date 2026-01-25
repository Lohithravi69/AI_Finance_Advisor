package com.aifa.finance.controller;

import com.aifa.finance.dto.GoalRequest;
import com.aifa.finance.dto.GoalResponse;
import com.aifa.finance.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(
            @RequestBody GoalRequest request,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        String userId = extractUserIdFromToken(token);
        GoalResponse response = goalService.createGoal(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<GoalResponse>> listGoals(
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        String userId = extractUserIdFromToken(token);
        List<GoalResponse> goals = goalService.listGoals(userId);
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalResponse> getGoal(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        String userId = extractUserIdFromToken(token);
        GoalResponse goal = goalService.getGoalById(id, userId);
        return ResponseEntity.ok(goal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        String userId = extractUserIdFromToken(token);
        goalService.deleteGoal(id, userId);
        return ResponseEntity.noContent().build();
    }

    private String extractUserIdFromToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return "test-user";
    }
}
