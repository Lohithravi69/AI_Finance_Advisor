package com.aifa.finance.dto;

import java.time.LocalDateTime;

public record NotificationResponse(
    Long id,
    String notificationType,
    String title,
    String message,
    Boolean isRead,
    Boolean isSent,
    String priority,
    Long referenceId,
    String referenceType,
    String actionUrl,
    LocalDateTime createdAt,
    LocalDateTime readAt,
    LocalDateTime sentAt
) {}
