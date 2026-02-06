package com.aifa.finance.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sync_logs", indexes = {
    @Index(name = "idx_sync_logs_user_id", columnList = "user_id"),
    @Index(name = "idx_sync_logs_status", columnList = "user_id, sync_status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "sync_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private SyncType syncType; // CLOUD_UPLOAD, CLOUD_DOWNLOAD, BIDIRECTIONAL

    @Column(name = "sync_status", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private SyncStatus syncStatus = SyncStatus.PENDING;

    @Column(name = "source_system", nullable = false, length = 100)
    private String sourceSystem; // e.g., "Google Drive", "OneDrive", "AWS S3"

    @Column(name = "items_synced")
    private Integer itemsSynced = 0;

    @Column(name = "conflicts_detected")
    private Integer conflictsDetected = 0;

    @Column(name = "conflicts_resolved")
    private Integer conflictsResolved = 0;

    @Column(name = "resolution_strategy", length = 50)
    private String resolutionStrategy; // KEEP_LOCAL, KEEP_REMOTE, MERGE, USER_CHOICE

    @Column(name = "sync_notes", columnDefinition = "TEXT")
    private String syncNotes;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "duration_seconds")
    private Long durationSeconds;

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

    public enum SyncType {
        CLOUD_UPLOAD, CLOUD_DOWNLOAD, BIDIRECTIONAL
    }

    public enum SyncStatus {
        PENDING, IN_PROGRESS, COMPLETED, FAILED, PARTIALLY_COMPLETED
    }

    public double getConflictResolutionRate() {
        if (conflictsDetected == 0) return 100.0;
        return (double) conflictsResolved / conflictsDetected * 100;
    }
}
