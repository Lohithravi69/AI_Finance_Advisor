package com.aifa.finance.controller;

import com.aifa.finance.dto.IncomeSourceRequest;
import com.aifa.finance.dto.IncomeSourceResponse;
import com.aifa.finance.service.IncomeSourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/income-sources")
@RequiredArgsConstructor
public class IncomeSourceController {

    private final IncomeSourceService incomeSourceService;

    @PostMapping
    public ResponseEntity<IncomeSourceResponse> createIncomeSource(@RequestParam Long userId, @RequestBody IncomeSourceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(incomeSourceService.createIncomeSource(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<IncomeSourceResponse>> getIncomeSources(@RequestParam Long userId) {
        return ResponseEntity.ok(incomeSourceService.getIncomeSourcesByUser(userId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<IncomeSourceResponse>> getActiveIncomeSources(@RequestParam Long userId) {
        return ResponseEntity.ok(incomeSourceService.getCurrentIncomeByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncomeSourceResponse> getIncomeSourceById(@PathVariable Long id) {
        return ResponseEntity.ok(incomeSourceService.getIncomeSourceById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncomeSourceResponse> updateIncomeSource(@PathVariable Long id, @RequestBody IncomeSourceRequest request) {
        return ResponseEntity.ok(incomeSourceService.updateIncomeSource(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncomeSource(@PathVariable Long id) {
        incomeSourceService.deleteIncomeSource(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/received")
    public ResponseEntity<IncomeSourceResponse> markAsReceived(@PathVariable Long id) {
        return ResponseEntity.ok(incomeSourceService.markAsReceived(id));
    }

    @GetMapping("/summary/monthly")
    public ResponseEntity<BigDecimal> getTotalMonthlyIncome(@RequestParam Long userId) {
        return ResponseEntity.ok(incomeSourceService.getTotalMonthlyIncome(userId));
    }

    @GetMapping("/summary/annual")
    public ResponseEntity<BigDecimal> getTotalAnnualIncome(@RequestParam Long userId) {
        return ResponseEntity.ok(incomeSourceService.getTotalAnnualIncome(userId));
    }
}
