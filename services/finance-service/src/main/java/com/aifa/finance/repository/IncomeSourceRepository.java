package com.aifa.finance.repository;

import com.aifa.finance.domain.IncomeSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface IncomeSourceRepository extends JpaRepository<IncomeSource, Long> {
    
    List<IncomeSource> findByUserId(Long userId);

    @Query("SELECT i FROM IncomeSource i WHERE i.user.id = :userId AND i.startDate <= CURRENT_DATE AND (i.endDate IS NULL OR i.endDate >= CURRENT_DATE) ORDER BY i.sourceName ASC")
    List<IncomeSource> findCurrentlyActiveByUserId(@Param("userId") Long userId);

    @Query("SELECT i FROM IncomeSource i WHERE i.user.id = :userId AND i.account.id = :accountId ORDER BY i.sourceName ASC")
    List<IncomeSource> findByUserIdAndAccountId(@Param("userId") Long userId, @Param("accountId") Long accountId);

    @Query("SELECT COALESCE(SUM(i.amount * CASE WHEN i.frequency = 'WEEKLY' THEN 52.0/12 WHEN i.frequency = 'BIWEEKLY' THEN 26.0/12 WHEN i.frequency = 'MONTHLY' THEN 1 WHEN i.frequency = 'QUARTERLY' THEN 4.0/12 ELSE 1.0/12 END), 0) FROM IncomeSource i WHERE i.user.id = :userId AND i.startDate <= CURRENT_DATE AND (i.endDate IS NULL OR i.endDate >= CURRENT_DATE)")
    Optional<BigDecimal> sumMonthlyIncomeByUserId(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(CASE WHEN i.frequency = 'WEEKLY' THEN i.amount * 52 WHEN i.frequency = 'BIWEEKLY' THEN i.amount * 26 WHEN i.frequency = 'MONTHLY' THEN i.amount * 12 WHEN i.frequency = 'QUARTERLY' THEN i.amount * 4 ELSE i.amount END), 0) FROM IncomeSource i WHERE i.user.id = :userId AND i.startDate <= CURRENT_DATE AND (i.endDate IS NULL OR i.endDate >= CURRENT_DATE)")
    Optional<BigDecimal> sumAnnualIncomeByUserId(@Param("userId") Long userId);

    Long countByUserId(Long userId);

    void deleteByUserId(Long userId);
}
