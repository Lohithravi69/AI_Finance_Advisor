package com.aifa.finance.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "saved_searches", indexes = {
    @Index(name = "idx_saved_searches_user_id", columnList = "user_id"),
    @Index(name = "idx_saved_searches_entity_type", columnList = "user_id, entity_type")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedSearch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "search_name", nullable = false)
    private String searchName;

    @Column(name = "entity_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private EntityType entityType; // TRANSACTION, ACCOUNT, INVESTMENT, GOAL, etc.

    @Column(name = "filter_criteria", columnDefinition = "TEXT", nullable = false)
    private String filterCriteria; // JSON string with filter conditions

    @Column(name = "sort_field")
    private String sortField;

    @Column(name = "sort_order", length = 10)
    private String sortOrder; // ASC, DESC

    @Column(name = "is_default")
    private Boolean isDefault = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum EntityType {
        TRANSACTION, ACCOUNT, INVESTMENT, GOAL, REPORT, NOTIFICATION
    }
}
