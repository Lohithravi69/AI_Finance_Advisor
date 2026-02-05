package com.aifa.finance.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports", indexes = {
    @Index(name = "idx_reports_user_id", columnList = "user_id"),
    @Index(name = "idx_reports_report_type", columnList = "user_id, report_type"),
    @Index(name = "idx_reports_generated_at", columnList = "user_id, generated_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "report_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    @Column(name = "report_name", nullable = false)
    private String reportName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "report_data", columnDefinition = "TEXT")
    private String reportData; // JSON format

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_format", length = 10)
    private String fileFormat; // PDF, CSV, EXCEL

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @Column(name = "is_scheduled", nullable = false)
    private Boolean isScheduled = false;

    @Column(name = "schedule_frequency", length = 50)
    private String scheduleFrequency; // DAILY, WEEKLY, MONTHLY

    @Column(name = "next_generation")
    private LocalDateTime nextGeneration;

    @PrePersist
    protected void onCreate() {
        generatedAt = LocalDateTime.now();
    }

    public enum ReportType {
        EXPENSE_SUMMARY,
        INCOME_SUMMARY,
        BUDGET_ANALYSIS,
        CATEGORY_BREAKDOWN,
        MONTHLY_COMPARISON,
        YEARLY_COMPARISON,
        NET_WORTH_TREND,
        CASH_FLOW,
        SAVINGS_RATE,
        INVESTMENT_PERFORMANCE,
        CUSTOM
    }
}
