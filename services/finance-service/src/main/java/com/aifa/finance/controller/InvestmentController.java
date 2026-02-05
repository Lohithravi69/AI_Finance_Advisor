package com.aifa.finance.controller;

import com.aifa.finance.dto.InvestmentRequest;
import com.aifa.finance.dto.InvestmentResponse;
import com.aifa.finance.service.InvestmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/investments")
@RequiredArgsConstructor
public class InvestmentController {

    private final InvestmentService investmentService;

    @PostMapping
    public ResponseEntity<InvestmentResponse> createInvestment(@RequestParam Long userId, @RequestBody InvestmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(investmentService.createInvestment(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<InvestmentResponse>> getInvestments(@RequestParam Long userId) {
        return ResponseEntity.ok(investmentService.getInvestmentsByUser(userId));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<InvestmentResponse>> getInvestmentsByAccount(@RequestParam Long userId, @PathVariable Long accountId) {
        return ResponseEntity.ok(investmentService.getInvestmentsByAccount(userId, accountId));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<InvestmentResponse>> getInvestmentsByType(@RequestParam Long userId, @PathVariable String type) {
        return ResponseEntity.ok(investmentService.getInvestmentsByType(userId, type));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvestmentResponse> getInvestmentById(@PathVariable Long id) {
        return ResponseEntity.ok(investmentService.getInvestmentById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvestmentResponse> updateInvestment(@PathVariable Long id, @RequestBody InvestmentRequest request) {
        return ResponseEntity.ok(investmentService.updateInvestment(id, request));
    }

    @PutMapping("/{id}/price")
    public ResponseEntity<InvestmentResponse> updatePrice(@PathVariable Long id, @RequestParam BigDecimal newPrice) {
        return ResponseEntity.ok(investmentService.updatePrice(id, newPrice));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvestment(@PathVariable Long id) {
        investmentService.deleteInvestment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary/total-invested")
    public ResponseEntity<BigDecimal> getTotalInvested(@RequestParam Long userId) {
        return ResponseEntity.ok(investmentService.getTotalInvested(userId));
    }

    @GetMapping("/summary/portfolio-value")
    public ResponseEntity<BigDecimal> getPortfolioValue(@RequestParam Long userId) {
        return ResponseEntity.ok(investmentService.getCurrentPortfolioValue(userId));
    }

    @GetMapping("/summary/gain-loss")
    public ResponseEntity<BigDecimal> getGainLoss(@RequestParam Long userId) {
        return ResponseEntity.ok(investmentService.getTotalGainLoss(userId));
    }
}
