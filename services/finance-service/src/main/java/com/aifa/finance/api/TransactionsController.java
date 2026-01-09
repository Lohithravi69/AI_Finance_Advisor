package com.aifa.finance.api;

import com.aifa.finance.api.dto.SummaryDto;
import com.aifa.finance.api.dto.TransactionDto;
import com.aifa.finance.service.TransactionService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionsController {

    private final TransactionService transactionService;

    @GetMapping
    public List<TransactionDto> list(@AuthenticationPrincipal Jwt jwt,
                                     @RequestParam(defaultValue = "10") @Min(1) int limit) {
        return transactionService.listTransactions(jwt, limit);
    }

    @GetMapping("/summary")
    public SummaryDto summary(@AuthenticationPrincipal Jwt jwt) {
        return transactionService.summary(jwt);
    }
}
