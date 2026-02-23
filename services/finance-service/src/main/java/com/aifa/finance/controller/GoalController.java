package com.aifa.finance.controller;

import com.aifa.finance.dto.GoalRequest;
import com.aifa.finance.dto.GoalResponse;
import com.aifa.finance.domain.User;
import com.aifa.finance.service.AuthService;
import com.aifa.finance.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {

    private final AuthService authService;
    private final GoalService goalService;

    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(
            @RequestBody GoalRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        User user = authService.getOrCreateUser(jwt);
        GoalResponse response = goalService.createGoal(request, user.getKeycloakId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<GoalResponse>> listGoals(
            @AuthenticationPrincipal Jwt jwt) {

        User user = authService.getOrCreateUser(jwt);
        List<GoalResponse> goals = goalService.listGoals(user.getKeycloakId());
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalResponse> getGoal(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        User user = authService.getOrCreateUser(jwt);
        GoalResponse goal = goalService.getGoalById(id, user.getKeycloakId());
        return ResponseEntity.ok(goal);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalResponse> updateGoal(
            @PathVariable Long id,
            @RequestBody GoalRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        User user = authService.getOrCreateUser(jwt);
        GoalResponse response = goalService.updateGoal(id, request, user.getKeycloakId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        User user = authService.getOrCreateUser(jwt);
        goalService.deleteGoal(id, user.getKeycloakId());
        return ResponseEntity.noContent().build();
    }
}
