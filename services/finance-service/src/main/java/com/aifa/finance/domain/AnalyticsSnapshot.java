package com.aifa.finance.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "analytics_snapshots", indexes = {
    @Index(name = "idx_analytics_user_id", columnList = "user_id"),
    @Index(name = "idx_analytics_snapshot_date", columnList = "user_id, snapshot_date")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "snapshot_date", nullable = false)
    private LocalDate snapshotDate;

    // Expense metrics
    @Column(name = "total_expenses", precision = 19, scale = 2)
    private BigDecimal totalExpenses;

    @Column(name = "total_income", precision = 19, scale = 2)
    private BigDecimal totalIncome;

    @Column(name = "net_savings", precision = 19, scale = 2)
    private BigDecimal netSavings;

    @Column(name = "savings_rate", precision = 5, scale = 2)
    private BigDecimal savingsRate;

    // Account metrics
    @Column(name = "total_assets", precision = 19, scale = 2)
    private BigDecimal totalAssets;

    @Column(name = "total_liabilities", precision = 19, scale = 2)
    private BigDecimal totalLiabilities;

    @Column(name = "net_worth", precision = 19, scale = 2)
    private BigDecimal netWorth;

    @Column(name = "liquid_assets", precision = 19, scale = 2)
    private BigDecimal liquidAssets;

    // Budget metrics
    @Column(name = "budget_utilization", precision = 5, scale = 2)
    private BigDecimal budgetUtilization;

    @Column(name = "over_budget_count")
    private Integer overBudgetCount;

    // Category metrics
    @Column(name = "top_category")
    private String topCategory;

    @Column(name = "top_category_amount", precision = 19, scale = 2)
    private BigDecimal topCategoryAmount;

    // Transaction metrics
    @Column(name = "transaction_count")
    private Integer transactionCount;

    @Column(name = "average_transaction", precision = 19, scale = 2)
    private BigDecimal averageTransaction;

    // Goals metrics
    @Column(name = "active_goals_count")
    private Integer activeGoalsCount;

    @Column(name = "completed_goals_count")
    private Integer completedGoalsCount;

    @Column(name = "total_goal_progress", precision = 5, scale = 2)
    private BigDecimal totalGoalProgress;
}
