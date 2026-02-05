package com.aifa.finance.service;

import com.aifa.finance.domain.Account;
import com.aifa.finance.domain.IncomeSource;
import com.aifa.finance.domain.User;
import com.aifa.finance.dto.IncomeSourceRequest;
import com.aifa.finance.dto.IncomeSourceResponse;
import com.aifa.finance.exception.ResourceNotFoundException;
import com.aifa.finance.repository.AccountRepository;
import com.aifa.finance.repository.IncomeSourceRepository;
import com.aifa.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class IncomeSourceService {

    private final IncomeSourceRepository incomeSourceRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public IncomeSourceResponse createIncomeSource(Long userId, IncomeSourceRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Account account = accountRepository.findById(request.accountId())
            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        IncomeSource incomeSource = IncomeSource.builder()
            .user(user)
            .account(account)
            .sourceName(request.sourceName())
            .description(request.description())
            .amount(request.amount())
            .frequency(IncomeSource.IncomeFrequency.valueOf(request.frequency()))
            .incomeType(request.incomeType())
            .startDate(request.startDate())
            .endDate(request.endDate())
            .nextExpected(calculateNextExpected(request.startDate(), IncomeSource.IncomeFrequency.valueOf(request.frequency())))
            .build();

        return toResponse(incomeSourceRepository.save(incomeSource));
    }

    @Transactional(readOnly = true)
    public List<IncomeSourceResponse> getIncomeSourcesByUser(Long userId) {
        return incomeSourceRepository.findByUserId(userId).stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<IncomeSourceResponse> getCurrentIncomeByUser(Long userId) {
        return incomeSourceRepository.findCurrentlyActiveByUserId(userId).stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public IncomeSourceResponse getIncomeSourceById(Long incomeSourceId) {
        IncomeSource incomeSource = incomeSourceRepository.findById(incomeSourceId)
            .orElseThrow(() -> new ResourceNotFoundException("Income source not found"));
        return toResponse(incomeSource);
    }

    @Transactional
    public IncomeSourceResponse updateIncomeSource(Long incomeSourceId, IncomeSourceRequest request) {
        IncomeSource incomeSource = incomeSourceRepository.findById(incomeSourceId)
            .orElseThrow(() -> new ResourceNotFoundException("Income source not found"));
        Account account = accountRepository.findById(request.accountId())
            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        incomeSource.setSourceName(request.sourceName());
        incomeSource.setDescription(request.description());
        incomeSource.setAmount(request.amount());
        incomeSource.setFrequency(IncomeSource.IncomeFrequency.valueOf(request.frequency()));
        incomeSource.setIncomeType(request.incomeType());
        incomeSource.setStartDate(request.startDate());
        incomeSource.setEndDate(request.endDate());
        incomeSource.setAccount(account);
        incomeSource.setNextExpected(calculateNextExpected(request.startDate(), IncomeSource.IncomeFrequency.valueOf(request.frequency())));

        return toResponse(incomeSourceRepository.save(incomeSource));
    }

    @Transactional
    public void deleteIncomeSource(Long incomeSourceId) {
        if (!incomeSourceRepository.existsById(incomeSourceId)) {
            throw new ResourceNotFoundException("Income source not found");
        }
        incomeSourceRepository.deleteById(incomeSourceId);
    }

    @Transactional
    public IncomeSourceResponse markAsReceived(Long incomeSourceId) {
        IncomeSource incomeSource = incomeSourceRepository.findById(incomeSourceId)
            .orElseThrow(() -> new ResourceNotFoundException("Income source not found"));
        
        incomeSource.setLastReceived(LocalDate.now());
        incomeSource.setNextExpected(calculateNextExpected(LocalDate.now(), incomeSource.getFrequency()));
        
        return toResponse(incomeSourceRepository.save(incomeSource));
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalMonthlyIncome(Long userId) {
        return incomeSourceRepository.sumMonthlyIncomeByUserId(userId).orElse(BigDecimal.ZERO);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalAnnualIncome(Long userId) {
        return incomeSourceRepository.sumAnnualIncomeByUserId(userId).orElse(BigDecimal.ZERO);
    }

    private LocalDate calculateNextExpected(LocalDate startDate, IncomeSource.IncomeFrequency frequency) {
        return switch (frequency) {
            case WEEKLY -> startDate.plusWeeks(1);
            case BIWEEKLY -> startDate.plusWeeks(2);
            case MONTHLY -> startDate.plusMonths(1);
            case QUARTERLY -> startDate.plusMonths(3);
            case ANNUAL -> startDate.plusYears(1);
        };
    }

    private IncomeSourceResponse toResponse(IncomeSource incomeSource) {
        return new IncomeSourceResponse(
            incomeSource.getId(),
            incomeSource.getSourceName(),
            incomeSource.getDescription(),
            incomeSource.getAccount().getId(),
            incomeSource.getAmount(),
            incomeSource.getFrequency().name(),
            incomeSource.getIncomeType(),
            incomeSource.getAnnualAmount(),
            incomeSource.getMonthlyAmount(),
            incomeSource.getStartDate(),
            incomeSource.getEndDate(),
            incomeSource.getLastReceived(),
            incomeSource.getNextExpected(),
            incomeSource.isCurrentlyActive(),
            incomeSource.getCreatedAt(),
            incomeSource.getUpdatedAt()
        );
    }
}
