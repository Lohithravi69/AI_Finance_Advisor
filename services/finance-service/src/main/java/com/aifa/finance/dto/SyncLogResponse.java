package com.aifa.finance.dto;

import com.aifa.finance.domain.SyncLog;

import java.time.LocalDateTime;

public record SyncLogResponse(
    Long id,
    String syncType,
    String syncStatus,
    String sourceSystem,
    Integer itemsSynced,
    Integer conflictsDetected,
    Integer conflictsResolved,
    String resolutionStrategy,
    String syncNotes,
    Double conflictResolutionRate,
    LocalDateTime startedAt,
    LocalDateTime completedAt,
    Long durationSeconds,
    LocalDateTime createdAt
) {
    public static SyncLogResponse fromEntity(SyncLog syncLog) {
        return new SyncLogResponse(
            syncLog.getId(),
            syncLog.getSyncType().toString(),
            syncLog.getSyncStatus().toString(),
            syncLog.getSourceSystem(),
            syncLog.getItemsSynced(),
            syncLog.getConflictsDetected(),
            syncLog.getConflictsResolved(),
            syncLog.getResolutionStrategy(),
            syncLog.getSyncNotes(),
            syncLog.getConflictResolutionRate(),
            syncLog.getStartedAt(),
            syncLog.getCompletedAt(),
            syncLog.getDurationSeconds(),
            syncLog.getCreatedAt()
        );
    }
}
