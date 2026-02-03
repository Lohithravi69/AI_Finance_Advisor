package com.aifa.finance.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "budget_alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    @Column(name = "alert_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AlertType alertType; // WARNING, EXCEEDED, RECOVERED

    @Column(nullable = false)
    private Integer percentage; // The threshold at which alert was triggered

    @Column(name = "triggered_at", nullable = false)
    private LocalDateTime triggeredAt;

    @PrePersist
    protected void onCreate() {
        triggeredAt = LocalDateTime.now();
    }

    public enum AlertType {
        WARNING,
        EXCEEDED,
        RECOVERED
    }
}
