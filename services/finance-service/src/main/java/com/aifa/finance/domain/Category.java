package com.aifa.finance.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Category entity for expense categorization.
 * Supports both predefined categories (system-wide) and user-defined custom categories.
 */
@Entity
@Table(name = "categories", indexes = {
    @Index(name = "idx_category_user_name", columnList = "user_id,name"),
    @Index(name = "idx_category_user_ispredefined", columnList = "user_id,is_predefined")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name; // e.g., "Groceries", "Transportation", "Entertainment"

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String icon; // e.g., "🛒", "🚗", "🎬", or emoji/icon identifier

    @Column(nullable = false)
    private String color; // HEX color code, e.g., "#FF5733"

    @Column(name = "is_predefined", nullable = false)
    private Boolean isPredefined; // true = system category, false = user-created

    @Column(name = "monthly_budget")
    private Double monthlyBudget; // Optional monthly budget for this category

    @Column(name = "spending_this_month")
    @Builder.Default
    private Double spendingThisMonth = 0.0;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Calculate percentage of monthly budget spent
     */
    public Double getPercentageSpent() {
        if (monthlyBudget == null || monthlyBudget <= 0) {
            return 0.0;
        }
        return (spendingThisMonth / monthlyBudget) * 100;
    }

    /**
     * Check if spending exceeds monthly budget
     */
    public Boolean isOverBudget() {
        if (monthlyBudget == null || monthlyBudget <= 0) {
            return false;
        }
        return spendingThisMonth > monthlyBudget;
    }
}
