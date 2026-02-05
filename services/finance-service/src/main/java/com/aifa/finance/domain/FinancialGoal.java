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
@Table(name = "financial_goals", indexes = {
    @Index(name = "idx_goals_user_id", columnList = "user_id"),
    @Index(name = "idx_goals_status", columnList = "user_id, status"),
    @Index(name = "idx_goals_category", columnList = "user_id, goal_category")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "goal_name", nullable = false)
    private String goalName;

    @Column(name = "goal_category", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private GoalCategory goalCategory;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "target_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal targetAmount;

    @Column(name = "current_amount", precision = 19, scale = 2)
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @Column(name = "progress_percentage", precision = 5, scale = 2)
    private BigDecimal progressPercentage = BigDecimal.ZERO;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private GoalStatus status = GoalStatus.NOT_STARTED;

    @Column(name = "priority", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private GoalPriority priority = GoalPriority.MEDIUM;

    @Column(name = "monthly_contribution", precision = 19, scale = 2)
    private BigDecimal monthlyContribution;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "is_recurring")
    private Boolean isRecurring = false;

    @Column(name = "completion_date")
    private LocalDate completionDate;

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
        calculateProgress();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateProgress();
    }

    public void calculateProgress() {
        if (targetAmount != null && targetAmount.compareTo(BigDecimal.ZERO) > 0) {
            progressPercentage = currentAmount.divide(targetAmount, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        }
    }

    public Long getDaysRemaining() {
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), targetDate);
    }

    public enum GoalCategory {
        SAVINGS,
        INVESTMENT,
        DEBT_PAYOFF,
        EMERGENCY_FUND,
        EDUCATION,
        HOUSE,
        VACATION,
        CAR,
        RETIREMENT,
        WEDDING,
        BUSINESS,
        OTHER
    }

    public enum GoalStatus {
        NOT_STARTED,
        IN_PROGRESS,
        PAUSED,
        COMPLETED,
        CANCELLED
    }

    public enum GoalPriority {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }
}
