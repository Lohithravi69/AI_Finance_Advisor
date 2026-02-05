package com.aifa.finance.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "alert_rules", indexes = {
    @Index(name = "idx_alert_rules_user_id", columnList = "user_id"),
    @Index(name = "idx_alert_rules_enabled", columnList = "user_id, is_enabled")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "rule_name", nullable = false)
    private String ruleName;

    @Column(name = "rule_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private AlertRuleType ruleType;

    @Column(name = "condition", nullable = false, columnDefinition = "TEXT")
    private String condition;

    @Column(name = "threshold_value", precision = 19, scale = 2)
    private BigDecimal thresholdValue;

    @Column(name = "notification_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private Notification.NotificationType notificationType;

    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = true;

    @Column(name = "frequency", length = 50)
    private String frequency; // DAILY, WEEKLY, MONTHLY, ONCE

    @Column(name = "last_triggered")
    private LocalDateTime lastTriggered;

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

    public enum AlertRuleType {
        BUDGET_EXCEEDED,
        EXPENSE_LIMIT,
        LOW_BALANCE,
        GOAL_PROGRESS,
        INCOME_RECEIVED,
        BILL_DUE,
        INVESTMENT_CHANGE,
        SAVINGS_TARGET,
        CUSTOM
    }
}
