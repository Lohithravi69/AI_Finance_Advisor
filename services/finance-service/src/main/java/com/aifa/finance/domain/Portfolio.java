package com.aifa.finance.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "portfolios", indexes = {
    @Index(name = "idx_portfolios_user_id", columnList = "user_id"),
    @Index(name = "idx_portfolios_account_id", columnList = "account_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "portfolio_name", nullable = false)
    private String portfolioName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "total_invested", precision = 19, scale = 2)
    private BigDecimal totalInvested;

    @Column(name = "current_value", precision = 19, scale = 2)
    private BigDecimal currentValue;

    @Column(name = "total_gain_loss", precision = 19, scale = 2)
    private BigDecimal totalGainLoss;

    @Column(name = "total_return_percentage", precision = 10, scale = 4)
    private BigDecimal totalReturnPercentage;

    @Column(name = "investment_count")
    private Integer investmentCount = 0;

    @Column(name = "allocation_stocks", precision = 5, scale = 2)
    private BigDecimal allocationStocks;

    @Column(name = "allocation_bonds", precision = 5, scale = 2)
    private BigDecimal allocationBonds;

    @Column(name = "allocation_crypto", precision = 5, scale = 2)
    private BigDecimal allocationCrypto;

    @Column(name = "allocation_other", precision = 5, scale = 2)
    private BigDecimal allocationOther;

    @Column(name = "rebalance_needed")
    private Boolean rebalanceNeeded = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
