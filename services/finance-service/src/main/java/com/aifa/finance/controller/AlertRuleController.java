package com.aifa.finance.controller;

import com.aifa.finance.dto.AlertRuleRequest;
import com.aifa.finance.dto.AlertRuleResponse;
import com.aifa.finance.service.AlertRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alert-rules")
@RequiredArgsConstructor
public class AlertRuleController {

    private final AlertRuleService alertRuleService;

    @PostMapping
    public ResponseEntity<AlertRuleResponse> createAlertRule(@RequestParam Long userId, @RequestBody AlertRuleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alertRuleService.createAlertRule(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<AlertRuleResponse>> getAlertRules(@RequestParam Long userId) {
        return ResponseEntity.ok(alertRuleService.getAlertRulesByUser(userId));
    }

    @GetMapping("/enabled")
    public ResponseEntity<List<AlertRuleResponse>> getEnabledAlertRules(@RequestParam Long userId) {
        return ResponseEntity.ok(alertRuleService.getEnabledAlertRules(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertRuleResponse> getAlertRuleById(@PathVariable Long id) {
        return ResponseEntity.ok(alertRuleService.getAlertRuleById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlertRuleResponse> updateAlertRule(@PathVariable Long id, @RequestBody AlertRuleRequest request) {
        return ResponseEntity.ok(alertRuleService.updateAlertRule(id, request));
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<AlertRuleResponse> toggleAlertRule(@PathVariable Long id) {
        return ResponseEntity.ok(alertRuleService.toggleAlertRule(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlertRule(@PathVariable Long id) {
        alertRuleService.deleteAlertRule(id);
        return ResponseEntity.noContent().build();
    }
}
