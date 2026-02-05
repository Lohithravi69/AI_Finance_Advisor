package com.aifa.finance.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_notifications_user_id", columnList = "user_id"),
    @Index(name = "idx_notifications_read", columnList = "user_id, is_read"),
    @Index(name = "idx_notifications_type", columnList = "user_id, notification_type"),
    @Index(name = "idx_notifications_created_at", columnList = "user_id, created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "notification_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "is_sent", nullable = false)
    private Boolean isSent = false;

    @Column(name = "priority", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private NotificationPriority priority = NotificationPriority.NORMAL;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "reference_type")
    private String referenceType;

    @Column(name = "action_url")
    private String actionUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum NotificationType {
        BUDGET_ALERT,
        EXPENSE_ALERT,
        GOAL_MILESTONE,
        INCOME_RECEIVED,
        BILL_DUE,
        INVESTMENT_UPDATE,
        ACCOUNT_UPDATE,
        TRANSACTION_ALERT,
        SAVINGS_REMINDER,
        SYSTEM_MESSAGE
    }

    public enum NotificationPriority {
        LOW,
        NORMAL,
        HIGH,
        CRITICAL
    }
}
