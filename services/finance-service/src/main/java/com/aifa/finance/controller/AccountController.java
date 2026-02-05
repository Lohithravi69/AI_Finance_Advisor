package com.aifa.finance.controller;

import com.aifa.finance.dto.AccountRequest;
import com.aifa.finance.dto.AccountResponse;
import com.aifa.finance.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@RequestParam Long userId, @RequestBody AccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAccounts(@RequestParam Long userId) {
        return ResponseEntity.ok(accountService.getAccountsByUser(userId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<AccountResponse>> getActiveAccounts(@RequestParam Long userId) {
        return ResponseEntity.ok(accountService.getActiveAccounts(userId));
    }

    @GetMapping("/primary")
    public ResponseEntity<AccountResponse> getPrimaryAccount(@RequestParam Long userId) {
        return ResponseEntity.ok(accountService.getPrimaryAccount(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<AccountResponse>> getAccountsByType(@RequestParam Long userId, @PathVariable String type) {
        return ResponseEntity.ok(accountService.getAccountsByType(userId, type));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountResponse> updateAccount(@PathVariable Long id, @RequestBody AccountRequest request) {
        return ResponseEntity.ok(accountService.updateAccount(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/net-worth")
    public ResponseEntity<BigDecimal> getNetWorth(@RequestParam Long userId) {
        return ResponseEntity.ok(accountService.calculateNetWorth(userId));
    }
}
