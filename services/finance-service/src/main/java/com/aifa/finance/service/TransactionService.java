package com.aifa.finance.service;

import com.aifa.finance.api.dto.SummaryDto;
import com.aifa.finance.api.dto.TransactionDto;
import com.aifa.finance.domain.Transaction;
import com.aifa.finance.domain.User;
import com.aifa.finance.repository.TransactionRepository;
import com.aifa.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public List<TransactionDto> listTransactions(Jwt jwt, int limit) {
        User user = ensureUser(jwt);
        return transactionRepository.findByUserOrderByTransactionDateDesc(user)
                .stream()
                .limit(limit)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public SummaryDto summary(Jwt jwt) {
        User user = ensureUser(jwt);
        List<Transaction> txs = transactionRepository.findByUserOrderByTransactionDateDesc(user);
        double expenses = txs.stream().mapToDouble(t -> Optional.ofNullable(t.getAmount()).orElse(0.0)).sum();
        double income = Optional.ofNullable(user.getMonthlyIncome()).orElse(0.0);
        double savings = income - expenses;
        String month = LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        return new SummaryDto(month, round(income), round(expenses), round(savings));
    }

    private TransactionDto toDto(Transaction t) {
        return new TransactionDto(
                t.getId(),
                t.getDescription(),
                t.getAmount(),
                t.getTransactionDate(),
                t.getMerchant(),
                t.getCategory()
        );
    }

    private User ensureUser(Jwt jwt) {
        String keycloakId = jwt.getSubject();
        String email = jwt.getClaim("email");
        if (email == null) {
            email = jwt.getClaim("preferred_username");
        }
        String name = jwt.getClaim("name");
        if (name == null) {
            name = email != null ? email : keycloakId;
        }

        return userRepository.findByKeycloakId(keycloakId)
                .orElseGet(() -> {
                    User u = new User();
                    u.setKeycloakId(keycloakId);
                    u.setEmail(email != null ? email : keycloakId + "@local");
                    u.setFullName(name);
                    u.setMonthlyIncome(0.0);
                    return userRepository.save(u);
                });
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
