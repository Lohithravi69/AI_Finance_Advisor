package com.aifa.finance.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "income_sources", indexes = {
    @Index(name = "idx_income_sources_user_id", columnList = "user_id"),
    @Index(name = "idx_income_sources_account_id", columnList = "account_id"),
    @Index(name = "idx_income_sources_user_account", columnList = "user_id, account_id"),
    @Index(name = "idx_income_sources_start_date", columnList = "user_id, start_date"),
    @Index(name = "idx_income_sources_end_date", columnList = "user_id, end_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncomeSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "source_name", nullable = false)
    private String sourceName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency", nullable = false, length = 50)
    private IncomeFrequency frequency;

    @Column(name = "income_type", length = 100)
    private String incomeType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "last_received")
    private LocalDate lastReceived;

    @Column(name = "next_expected")
    private LocalDate nextExpected;

    @Column(name = "created_at", nullable = false, updatable = false)
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

    public enum IncomeFrequency {
        WEEKLY,
        BIWEEKLY,
        MONTHLY,
        QUARTERLY,
        ANNUAL
    }

    /**
     * Calculate annual amount based on frequency
     */
    public BigDecimal getAnnualAmount() {
        return switch (frequency) {
            case WEEKLY -> amount.multiply(BigDecimal.valueOf(52));
            case BIWEEKLY -> amount.multiply(BigDecimal.valueOf(26));
            case MONTHLY -> amount.multiply(BigDecimal.valueOf(12));
            case QUARTERLY -> amount.multiply(BigDecimal.valueOf(4));
            case ANNUAL -> amount;
        };
    }

    /**
     * Calculate monthly amount based on frequency
     */
    public BigDecimal getMonthlyAmount() {
        return switch (frequency) {
            case WEEKLY -> amount.multiply(BigDecimal.valueOf(52)).divide(BigDecimal.valueOf(12), 2, java.math.RoundingMode.HALF_UP);
            case BIWEEKLY -> amount.multiply(BigDecimal.valueOf(26)).divide(BigDecimal.valueOf(12), 2, java.math.RoundingMode.HALF_UP);
            case MONTHLY -> amount;
            case QUARTERLY -> amount.multiply(BigDecimal.valueOf(4)).divide(BigDecimal.valueOf(12), 2, java.math.RoundingMode.HALF_UP);
            case ANNUAL -> amount.divide(BigDecimal.valueOf(12), 2, java.math.RoundingMode.HALF_UP);
        };
    }

    /**
     * Check if income source is currently active based on dates
     */
    public Boolean isCurrentlyActive() {
        LocalDate now = LocalDate.now();
        return startDate.isBefore(now.plusDays(1)) && 
               (endDate == null || endDate.isAfter(now.minusDays(1)));
    }
}
