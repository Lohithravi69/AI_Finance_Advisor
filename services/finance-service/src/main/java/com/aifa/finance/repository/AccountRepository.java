package com.aifa.finance.repository;

import com.aifa.finance.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    List<Account> findByUserId(Long userId);

    @Query("SELECT a FROM Account a WHERE a.user.id = :userId AND a.isActive = true ORDER BY a.isPrimary DESC, a.accountName ASC")
    List<Account> findActiveByUserId(@Param("userId") Long userId);

    Optional<Account> findByUserIdAndIsPrimaryTrue(Long userId);

    @Query("SELECT a FROM Account a WHERE a.user.id = :userId AND a.accountType = :accountType ORDER BY a.accountName ASC")
    List<Account> findByUserIdAndAccountType(@Param("userId") Long userId, @Param("accountType") Account.AccountType accountType);

    @Query("SELECT COALESCE(SUM(a.currentBalance), 0) FROM Account a WHERE a.user.id = :userId AND a.isActive = true")
    Optional<BigDecimal> sumBalanceByUserId(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(a.currentBalance), 0) FROM Account a WHERE a.user.id = :userId AND a.accountType = :accountType AND a.isActive = true")
    Optional<BigDecimal> sumBalanceByUserIdAndAccountType(@Param("userId") Long userId, @Param("accountType") Account.AccountType accountType);

    Long countByUserId(Long userId);

    void deleteByUserId(Long userId);
}
