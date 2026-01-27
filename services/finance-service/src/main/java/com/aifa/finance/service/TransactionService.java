package com.aifa.finance.service;

import com.aifa.finance.api.dto.SummaryDto;
import com.aifa.finance.api.dto.TransactionDto;
import com.aifa.finance.dto.TransactionRequest;
import com.aifa.finance.dto.TransactionResponse;
import com.aifa.finance.dto.FinancialSummaryResponse;
import com.aifa.finance.domain.Transaction;
import com.aifa.finance.domain.User;
import com.aifa.finance.repository.TransactionRepository;
import com.aifa.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
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

    public List<TransactionResponse> listTransactions(String userId, int limit) {
        User user = ensureUserByString(userId);
        return transactionRepository.findByUserOrderByTransactionDateDesc(user)
                .stream()
                .limit(limit)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TransactionResponse createTransaction(TransactionRequest request, String userId) {
        User user = ensureUserByString(userId);
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setTransactionDate(request.getTransactionDate());
        
        Transaction saved = transactionRepository.save(transaction);
        return toResponse(saved);
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

    public FinancialSummaryResponse getMonthlySummary(String userId, YearMonth yearMonth) {
        User user = ensureUserByString(userId);
        List<Transaction> transactions = transactionRepository.findByUserOrderByTransactionDateDesc(user);
        
        double income = transactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase("INCOME") && 
                        t.getTransactionDate().getYear() == yearMonth.getYear() &&
                        t.getTransactionDate().getMonthValue() == yearMonth.getMonthValue())
                .mapToDouble(t -> t.getAmount() != null ? t.getAmount() : 0.0)
                .sum();
        
        double expenses = transactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase("EXPENSE") && 
                        t.getTransactionDate().getYear() == yearMonth.getYear() &&
                        t.getTransactionDate().getMonthValue() == yearMonth.getMonthValue())
                .mapToDouble(t -> t.getAmount() != null ? t.getAmount() : 0.0)
                .sum();
        
        int count = (int) transactions.stream()
                .filter(t -> t.getTransactionDate().getYear() == yearMonth.getYear() &&
                        t.getTransactionDate().getMonthValue() == yearMonth.getMonthValue())
                .count();
        
        return FinancialSummaryResponse.builder()
                .totalIncome(round(income))
                .totalExpenses(round(expenses))
                .netSavings(round(income - expenses))
                .transactionCount(count)
                .period(yearMonth.toString())
                .build();
    }

    public FinancialSummaryResponse getYearlySummary(String userId, String year) {
        User user = ensureUserByString(userId);
        List<Transaction> transactions = transactionRepository.findByUserOrderByTransactionDateDesc(user);
        int yearVal = year.isEmpty() ? LocalDate.now().getYear() : Integer.parseInt(year);
        
        double income = transactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase("INCOME") && 
                        t.getTransactionDate().getYear() == yearVal)
                .mapToDouble(t -> t.getAmount() != null ? t.getAmount() : 0.0)
                .sum();
        
        double expenses = transactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase("EXPENSE") && 
                        t.getTransactionDate().getYear() == yearVal)
                .mapToDouble(t -> t.getAmount() != null ? t.getAmount() : 0.0)
                .sum();
        
        int count = (int) transactions.stream()
                .filter(t -> t.getTransactionDate().getYear() == yearVal)
                .count();
        
        return FinancialSummaryResponse.builder()
                .totalIncome(round(income))
                .totalExpenses(round(expenses))
                .netSavings(round(income - expenses))
                .transactionCount(count)
                .period(String.valueOf(yearVal))
                .build();
    }

    private TransactionDto toDto(Transaction t) {
        return new TransactionDto(
                t.getId(),
                t.getType(),
                t.getDescription(),
                t.getAmount(),
                t.getTransactionDate(),
                t.getMerchant(),
                t.getCategory()
        );
    }

    private TransactionResponse toResponse(Transaction t) {
        return TransactionResponse.builder()
                .id(t.getId())
                .userId(t.getUser() != null ? t.getUser().getId() : null)
                .type(t.getType())
                .amount(t.getAmount())
                .description(t.getDescription())
                .transactionDate(t.getTransactionDate())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
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

        Optional<User> existing = userRepository.findByKeycloakId(keycloakId);
        if (existing.isPresent()) {
            return existing.get();
        }

        User u = new User();
        u.setKeycloakId(keycloakId);
        String emailVal = (email != null) ? email : keycloakId + "@local";
        String nameVal = (name != null) ? name : (email != null ? email : keycloakId);
        u.setEmail(emailVal);
        u.setFullName(nameVal);
        u.setMonthlyIncome(0.0);
        return userRepository.save(u);
    }

    private User ensureUserByString(String userId) {
        // Find existing user or create test user
        Optional<User> existing = userRepository.findByKeycloakId(userId);
        if (existing.isPresent()) {
            return existing.get();
        }

        User u = new User();
        u.setKeycloakId(userId);
        u.setEmail(userId + "@test.local");
        u.setFullName("Test User");
        u.setMonthlyIncome(0.0);
        return userRepository.save(u);
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
