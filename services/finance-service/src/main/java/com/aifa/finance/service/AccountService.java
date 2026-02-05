package com.aifa.finance.service;

import com.aifa.finance.domain.Account;
import com.aifa.finance.domain.User;
import com.aifa.finance.dto.AccountRequest;
import com.aifa.finance.dto.AccountResponse;
import com.aifa.finance.exception.ResourceNotFoundException;
import com.aifa.finance.repository.AccountRepository;
import com.aifa.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Transactional
    public AccountResponse createAccount(Long userId, AccountRequest request) {
        log.info("Creating account for user: {}", userId);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.isPrimary()) {
            accountRepository.findActiveByUserId(userId).forEach(acc -> {
                acc.setIsPrimary(false);
                accountRepository.save(acc);
            });
        }

        Account account = Account.builder()
            .user(user)
            .accountName(request.accountName())
            .accountType(Account.AccountType.valueOf(request.accountType()))
            .institutionName(request.institutionName())
            .accountNumber(request.accountNumber())
            .currentBalance(request.currentBalance())
            .availableBalance(request.availableBalance())
            .currency(request.currency())
            .accountColor(request.accountColor())
            .isPrimary(request.isPrimary())
            .isActive(true)
            .build();

        Account saved = accountRepository.save(account);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsByUser(Long userId) {
        return accountRepository.findByUserId(userId).stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getActiveAccounts(Long userId) {
        return accountRepository.findActiveByUserId(userId).stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public AccountResponse getPrimaryAccount(Long userId) {
        return accountRepository.findByUserIdAndIsPrimaryTrue(userId)
            .map(this::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("No primary account found"));
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccountById(Long accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        return toResponse(account);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsByType(Long userId, String accountType) {
        return accountRepository.findByUserIdAndAccountType(userId, Account.AccountType.valueOf(accountType))
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional
    public AccountResponse updateAccount(Long accountId, AccountRequest request) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (request.isPrimary() && !account.getIsPrimary()) {
            accountRepository.findActiveByUserId(account.getUser().getId()).forEach(acc -> {
                if (!acc.getId().equals(accountId)) {
                    acc.setIsPrimary(false);
                    accountRepository.save(acc);
                }
            });
        }

        account.setAccountName(request.accountName());
        account.setInstitutionName(request.institutionName());
        account.setAccountType(Account.AccountType.valueOf(request.accountType()));
        account.setCurrentBalance(request.currentBalance());
        account.setAvailableBalance(request.availableBalance());
        account.setCurrency(request.currency());
        account.setAccountColor(request.accountColor());
        account.setIsPrimary(request.isPrimary());

        return toResponse(accountRepository.save(account));
    }

    @Transactional
    public void deleteAccount(Long accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new ResourceNotFoundException("Account not found");
        }
        accountRepository.deleteById(accountId);
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateNetWorth(Long userId) {
        BigDecimal totalAssets = accountRepository.sumBalanceByUserId(userId).orElse(BigDecimal.ZERO);
        BigDecimal creditCardDebt = accountRepository.sumBalanceByUserIdAndAccountType(userId, Account.AccountType.CREDIT_CARD).orElse(BigDecimal.ZERO);
        BigDecimal loanDebt = accountRepository.sumBalanceByUserIdAndAccountType(userId, Account.AccountType.LOAN).orElse(BigDecimal.ZERO);
        return totalAssets.subtract(creditCardDebt).subtract(loanDebt);
    }

    private AccountResponse toResponse(Account account) {
        return new AccountResponse(
            account.getId(),
            account.getAccountName(),
            account.getAccountType().name(),
            account.getInstitutionName(),
            account.getAccountNumber(),
            account.getCurrentBalance(),
            account.getAvailableBalance(),
            account.getNetBalance(),
            account.getCurrency(),
            account.getAccountColor(),
            account.getIsActive(),
            account.getIsPrimary(),
            account.getCreatedAt(),
            account.getUpdatedAt()
        );
    }
}
