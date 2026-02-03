package com.aifa.finance.model;

import com.aifa.finance.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_preferences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String currencyPreference;

    @Column(nullable = false)
    private String timezone;

    @Column(nullable = false)
    private Boolean emailNotifications;

    @Column(nullable = false)
    private Boolean pushNotifications;

    @Column(nullable = false)
    private Boolean budgetAlerts;

    @Column(nullable = false)
    private Boolean goalMilestoneNotifications;

    @Column(nullable = false)
    private Boolean investmentUpdates;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
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
}
