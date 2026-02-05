package com.aifa.finance.repository;

import com.aifa.finance.domain.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    List<Portfolio> findByUserIdOrderByUpdatedAtDesc(Long userId);

    List<Portfolio> findByUserIdAndAccountId(Long userId, Long accountId);

    @Query("SELECT COUNT(p) FROM Portfolio p WHERE p.user.id = :userId")
    Long countByUserId(Long userId);

    void deleteByUserIdAndId(Long userId, Long portfolioId);
}
