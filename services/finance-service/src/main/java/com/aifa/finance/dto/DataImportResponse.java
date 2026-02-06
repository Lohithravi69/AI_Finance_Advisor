package com.aifa.finance.dto;

import com.aifa.finance.domain.DataImport;

import java.time.LocalDateTime;

public record DataImportResponse(
    Long id,
    String importType,
    String fileName,
    String importStatus,
    Integer totalRecords,
    Integer processedRecords,
    Integer successCount,
    Integer errorCount,
    String errorDetails,
    Double successRate,
    LocalDateTime startedAt,
    LocalDateTime completedAt,
    LocalDateTime createdAt
) {
    public static DataImportResponse fromEntity(DataImport dataImport) {
        return new DataImportResponse(
            dataImport.getId(),
            dataImport.getImportType().toString(),
            dataImport.getFileName(),
            dataImport.getImportStatus().toString(),
            dataImport.getTotalRecords(),
            dataImport.getProcessedRecords(),
            dataImport.getSuccessCount(),
            dataImport.getErrorCount(),
            dataImport.getErrorDetails(),
            dataImport.getSuccessRate(),
            dataImport.getStartedAt(),
            dataImport.getCompletedAt(),
            dataImport.getCreatedAt()
        );
    }
}
