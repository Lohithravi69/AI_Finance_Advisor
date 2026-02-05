package com.aifa.finance.repository;

import com.aifa.finance.domain.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {

    List<Investment> findByUserIdOrderByPurchaseDateDesc(Long userId);

    List<Investment> findByUserIdAndAccountId(Long userId, Long accountId);

    List<Investment> findByUserIdAndInvestmentType(Long userId, String investmentType);

    @Query("SELECT SUM(i.totalCost) FROM Investment i WHERE i.user.id = :userId")
    BigDecimal sumTotalCostByUserId(Long userId);

    @Query("SELECT SUM(i.currentValue) FROM Investment i WHERE i.user.id = :userId")
    BigDecimal sumCurrentValueByUserId(Long userId);

    @Query("SELECT SUM(i.gainLoss) FROM Investment i WHERE i.user.id = :userId")
    BigDecimal sumGainLossByUserId(Long userId);

    @Query("SELECT COUNT(i) FROM Investment i WHERE i.user.id = :userId")
    Long countByUserId(Long userId);

    List<Investment> findByUserIdAndSymbol(Long userId, String symbol);
}
