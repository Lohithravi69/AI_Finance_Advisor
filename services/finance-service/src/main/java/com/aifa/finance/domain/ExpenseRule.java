package com.aifa.finance.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * ExpenseRule entity for automatic expense categorization.
 * Matches transaction descriptions/merchants against patterns to auto-categorize.
 */
@Entity
@Table(name = "expense_rules", indexes = {
    @Index(name = "idx_rule_user_category", columnList = "user_id,category_id"),
    @Index(name = "idx_rule_user_pattern", columnList = "user_id,pattern")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private String pattern; // Keywords or patterns to match (case-insensitive)
                            // e.g., "walmart", "target", "grocery"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RuleType ruleType = RuleType.KEYWORD; // KEYWORD, MERCHANT, REGEX

    @Column(name = "match_type", nullable = false)
    @Builder.Default
    private String matchType = "CONTAINS"; // CONTAINS, EXACT, STARTS_WITH

    @Column(name = "priority", nullable = false)
    @Builder.Default
    private Integer priority = 0; // Higher priority rules are checked first (0-100)

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "match_count")
    @Builder.Default
    private Long matchCount = 0L; // How many times this rule matched (for ML ranking)

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
     * Check if this rule matches a given text (description or merchant)
     */
    public boolean matches(String text) {
        if (text == null || !isActive) {
            return false;
        }

        String lowerText = text.toLowerCase();
        String lowerPattern = pattern.toLowerCase();

        return switch (matchType) {
            case "EXACT" -> lowerText.equals(lowerPattern);
            case "STARTS_WITH" -> lowerText.startsWith(lowerPattern);
            case "CONTAINS" -> lowerText.contains(lowerPattern);
            default -> false;
        };
    }

    /**
     * Enum for different types of matching rules
     */
    public enum RuleType {
        KEYWORD,    // Matches by keywords in description
        MERCHANT,   // Matches by merchant name
        REGEX       // Matches by regex pattern (advanced)
    }
}
