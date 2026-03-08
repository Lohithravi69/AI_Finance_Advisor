package com.aifa.finance.dto;

import java.time.LocalDateTime;

public record ReportRequest(
    String reportName,
    String reportType,
    String description,
    LocalDateTime startDate,
    LocalDateTime endDate,
    String fileFormat,
    Boolean isScheduled,
    String scheduleFrequency
) {}
