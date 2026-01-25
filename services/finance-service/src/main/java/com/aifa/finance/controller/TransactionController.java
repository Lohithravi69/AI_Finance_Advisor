package com.aifa.finance.controller;

import com.aifa.finance.dto.TransactionRequest;
import com.aifa.finance.dto.TransactionResponse;
import com.aifa.finance.dto.FinancialSummaryResponse;
import com.aifa.finance.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @RequestBody TransactionRequest request,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        String userId = extractUserIdFromToken(token);
        TransactionResponse response = transactionService.createTransaction(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> listTransactions(
            @RequestParam(defaultValue = "50") int limit,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        String userId = extractUserIdFromToken(token);
        List<TransactionResponse> transactions = transactionService.listTransactions(userId, limit);
        return ResponseEntity.ok(transactions);
    }

    private String extractUserIdFromToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return "test-user";
    }
}
