package com.aifa.finance.dto;

import java.time.LocalDateTime;

public record ReportResponse(
    Long id,
    String reportName,
    String reportType,
    String description,
    LocalDateTime startDate,
    LocalDateTime endDate,
    String reportData,
    String filePath,
    String fileFormat,
    LocalDateTime generatedAt,
    Boolean isScheduled,
    String scheduleFrequency,
    LocalDateTime nextGeneration
) {}
