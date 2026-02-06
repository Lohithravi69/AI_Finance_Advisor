package com.aifa.finance.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "data_imports", indexes = {
    @Index(name = "idx_data_imports_user_id", columnList = "user_id"),
    @Index(name = "idx_data_imports_status", columnList = "user_id, import_status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataImport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "import_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private ImportType importType; // CSV, BANK_API, QIF, OFX, etc.

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "import_status", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private ImportStatus importStatus = ImportStatus.PENDING;

    @Column(name = "total_records")
    private Integer totalRecords = 0;

    @Column(name = "processed_records")
    private Integer processedRecords = 0;

    @Column(name = "success_count")
    private Integer successCount = 0;

    @Column(name = "error_count")
    private Integer errorCount = 0;

    @Column(name = "error_details", columnDefinition = "TEXT")
    private String errorDetails;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

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

    public enum ImportType {
        CSV, BANK_API, QIF, OFX, JSON, SPREADSHEET
    }

    public enum ImportStatus {
        PENDING, IN_PROGRESS, COMPLETED, FAILED, PARTIALLY_COMPLETED
    }

    public double getSuccessRate() {
        if (totalRecords == 0) return 0.0;
        return (double) successCount / totalRecords * 100;
    }
}
