package com.aifa.finance.service;

import com.aifa.finance.domain.AnalyticsSnapshot;
import com.aifa.finance.domain.User;
import com.aifa.finance.dto.AnalyticsSnapshotResponse;
import com.aifa.finance.exception.ResourceNotFoundException;
import com.aifa.finance.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AnalyticsSnapshotRepository analyticsSnapshotRepository;
    private final UserRepository userRepository;

    @Transactional
    public AnalyticsSnapshotResponse generateDailySnapshot(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LocalDate today = LocalDate.now();

        // Create simple snapshot with placeholder data
        AnalyticsSnapshot snapshot = analyticsSnapshotRepository
            .findByUserIdAndSnapshotDate(userId, today)
            .orElse(AnalyticsSnapshot.builder()
                .user(user)
                .snapshotDate(today)
                .totalExpenses(BigDecimal.ZERO)
                .totalIncome(BigDecimal.ZERO)
                .netSavings(BigDecimal.ZERO)
                .savingsRate(BigDecimal.ZERO)
                .totalAssets(BigDecimal.ZERO)
                .totalLiabilities(BigDecimal.ZERO)
                .netWorth(BigDecimal.ZERO)
                .liquidAssets(BigDecimal.ZERO)
                .budgetUtilization(BigDecimal.ZERO)
                .overBudgetCount(0)
                .transactionCount(0)
                .averageTransaction(BigDecimal.ZERO)
                .activeGoalsCount(0)
                .completedGoalsCount(0)
                .totalGoalProgress(BigDecimal.ZERO)
                .build());

        snapshot = analyticsSnapshotRepository.save(snapshot);
        return toResponse(snapshot);
    }

    @Transactional(readOnly = true)
    public List<AnalyticsSnapshotResponse> getSnapshotHistory(Long userId, LocalDate startDate, LocalDate endDate) {
        return analyticsSnapshotRepository.findByUserIdAndDateRange(userId, startDate, endDate)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AnalyticsSnapshotResponse getLatestSnapshot(Long userId) {
        return analyticsSnapshotRepository.findLatestSnapshot(userId)
            .map(this::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("No analytics data available"));
    }

    @Transactional(readOnly = true)
    public List<AnalyticsSnapshotResponse> getLast30Days(Long userId) {
        return analyticsSnapshotRepository.findLast30Days(userId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    private AnalyticsSnapshotResponse toResponse(AnalyticsSnapshot snapshot) {
        return new AnalyticsSnapshotResponse(
            snapshot.getId(),
            snapshot.getSnapshotDate(),
            snapshot.getTotalExpenses(),
            snapshot.getTotalIncome(),
            snapshot.getNetSavings(),
            snapshot.getSavingsRate(),
            snapshot.getTotalAssets(),
            snapshot.getTotalLiabilities(),
            snapshot.getNetWorth(),
            snapshot.getLiquidAssets(),
            snapshot.getBudgetUtilization(),
            snapshot.getOverBudgetCount(),
            snapshot.getTopCategory(),
            snapshot.getTopCategoryAmount(),
            snapshot.getTransactionCount(),
            snapshot.getAverageTransaction(),
            snapshot.getActiveGoalsCount(),
            snapshot.getCompletedGoalsCount(),
            snapshot.getTotalGoalProgress()
        );
    }
}
