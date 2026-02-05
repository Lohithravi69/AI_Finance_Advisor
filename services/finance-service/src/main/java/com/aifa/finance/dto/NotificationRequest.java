package com.aifa.finance.dto;

import java.time.LocalDateTime;

public record NotificationRequest(
    String notificationType,
    String title,
    String message,
    String priority,
    Long referenceId,
    String referenceType,
    String actionUrl
) {}
