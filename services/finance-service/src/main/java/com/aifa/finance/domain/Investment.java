package com.aifa.finance.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "investments", indexes = {
    @Index(name = "idx_investments_user_id", columnList = "user_id"),
    @Index(name = "idx_investments_account_id", columnList = "account_id"),
    @Index(name = "idx_investments_user_account", columnList = "user_id, account_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "symbol", nullable = false, length = 20)
    private String symbol;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "investment_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private InvestmentType investmentType;

    @Column(name = "quantity", nullable = false, precision = 19, scale = 8)
    private BigDecimal quantity;

    @Column(name = "purchase_price", nullable = false, precision = 19, scale = 8)
    private BigDecimal purchasePrice;

    @Column(name = "current_price", precision = 19, scale = 8)
    private BigDecimal currentPrice;

    @Column(name = "total_cost", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalCost;

    @Column(name = "current_value", precision = 19, scale = 2)
    private BigDecimal currentValue;

    @Column(name = "gain_loss", precision = 19, scale = 2)
    private BigDecimal gainLoss;

    @Column(name = "gain_loss_percentage", precision = 10, scale = 4)
    private BigDecimal gainLossPercentage;

    @Column(name = "purchase_date", nullable = false)
    private LocalDateTime purchaseDate;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "currency", length = 3)
    private String currency;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        lastUpdated = LocalDateTime.now();
        calculateMetrics();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateMetrics();
    }

    public void calculateMetrics() {
        if (currentPrice != null && currentPrice.compareTo(BigDecimal.ZERO) > 0) {
            currentValue = currentPrice.multiply(quantity);
            gainLoss = currentValue.subtract(totalCost);
            gainLossPercentage = gainLoss.divide(totalCost, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        }
    }

    public enum InvestmentType {
        STOCK,
        BOND,
        MUTUAL_FUND,
        ETF,
        CRYPTOCURRENCY,
        COMMODITY,
        OPTION,
        REAL_ESTATE,
        OTHER
    }
}
