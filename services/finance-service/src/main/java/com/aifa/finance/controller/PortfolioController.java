package com.aifa.finance.controller;

import com.aifa.finance.dto.PortfolioRequest;
import com.aifa.finance.dto.PortfolioResponse;
import com.aifa.finance.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @PostMapping
    public ResponseEntity<PortfolioResponse> createPortfolio(@RequestParam Long userId, @RequestBody PortfolioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(portfolioService.createPortfolio(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<PortfolioResponse>> getPortfolios(@RequestParam Long userId) {
        return ResponseEntity.ok(portfolioService.getPortfoliosByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PortfolioResponse> getPortfolioById(@PathVariable Long id) {
        return ResponseEntity.ok(portfolioService.getPortfolioById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PortfolioResponse> updatePortfolio(@PathVariable Long id, @RequestBody PortfolioRequest request) {
        return ResponseEntity.ok(portfolioService.updatePortfolio(id, request));
    }

    @PostMapping("/{id}/rebalance")
    public ResponseEntity<PortfolioResponse> rebalancePortfolio(@PathVariable Long id) {
        return ResponseEntity.ok(portfolioService.rebalancePortfolio(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePortfolio(@PathVariable Long id) {
        portfolioService.deletePortfolio(id);
        return ResponseEntity.noContent().build();
    }
}
