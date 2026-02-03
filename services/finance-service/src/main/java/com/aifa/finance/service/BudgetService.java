package com.aifa.finance.service;

import com.aifa.finance.domain.Budget;
import com.aifa.finance.domain.BudgetAlert;
import com.aifa.finance.domain.User;
import com.aifa.finance.dto.BudgetRequest;
import com.aifa.finance.dto.BudgetResponse;
import com.aifa.finance.dto.BudgetAlertResponse;
import com.aifa.finance.repository.BudgetRepository;
import com.aifa.finance.repository.BudgetAlertRepository;
import com.aifa.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final BudgetAlertRepository budgetAlertRepository;
    private final UserRepository userRepository;

    /**
     * Create a new budget
     */
    public BudgetResponse createBudget(Long userId, BudgetRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Budget budget = Budget.builder()
                .user(user)
                .name(request.getName())
                .category(request.getCategory())
                .monthlyLimit(request.getMonthlyLimit())
                .spentAmount(BigDecimal.ZERO)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .alertThreshold(request.getAlertThreshold() != null ? request.getAlertThreshold() : 80)
                .build();

        Budget savedBudget = budgetRepository.save(budget);
        return toBudgetResponse(savedBudget);
    }

    /**
     * Get all budgets for a user
     */
    public List<BudgetResponse> getBudgetsByUser(Long userId) {
        return budgetRepository.findByUserId(userId).stream()
                .map(this::toBudgetResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get active budgets for a specific date
     */
    public List<BudgetResponse> getActiveBudgets(Long userId, LocalDate date) {
        return budgetRepository.findActiveBudgetsByUserAndDate(userId, date).stream()
                .map(this::toBudgetResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific budget
     */
    public BudgetResponse getBudget(Long budgetId, Long userId) {
        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));
        return toBudgetResponse(budget);
    }

    /**
     * Update a budget
     */
    public BudgetResponse updateBudget(Long budgetId, Long userId, BudgetRequest request) {
        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        if (request.getName() != null) budget.setName(request.getName());
        if (request.getCategory() != null) budget.setCategory(request.getCategory());
        if (request.getMonthlyLimit() != null) budget.setMonthlyLimit(request.getMonthlyLimit());
        if (request.getStartDate() != null) budget.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) budget.setEndDate(request.getEndDate());
        if (request.getAlertThreshold() != null) budget.setAlertThreshold(request.getAlertThreshold());

        Budget updated = budgetRepository.save(budget);
        return toBudgetResponse(updated);
    }

    /**
     * Delete a budget
     */
    public void deleteBudget(Long budgetId, Long userId) {
        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));
        budgetRepository.delete(budget);
    }

    /**
     * Update spent amount and check for alerts
     */
    public void updateSpentAmount(Long budgetId, BigDecimal amount) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        BigDecimal previousAmount = budget.getSpentAmount();
        budget.setSpentAmount(previousAmount.add(amount));

        checkAndCreateAlerts(budget, previousAmount);
        budgetRepository.save(budget);
    }

    /**
     * Check if spending exceeds thresholds and create alerts
     */
    private void checkAndCreateAlerts(Budget budget, BigDecimal previousAmount) {
        double percentageSpent = (budget.getSpentAmount().doubleValue() / 
                budget.getMonthlyLimit().doubleValue()) * 100;
        double previousPercentage = (previousAmount.doubleValue() / 
                budget.getMonthlyLimit().doubleValue()) * 100;
        
        int threshold = budget.getAlertThreshold() != null ? budget.getAlertThreshold() : 80;

        // Check if crossed warning threshold
        if (previousPercentage < threshold && percentageSpent >= threshold) {
            createAlert(budget, BudgetAlert.AlertType.WARNING, threshold);
        }

        // Check if exceeded 100%
        if (previousPercentage < 100 && percentageSpent >= 100) {
            createAlert(budget, BudgetAlert.AlertType.EXCEEDED, 100);
        }

        // Check if recovered below threshold
        if (previousPercentage >= threshold && percentageSpent < threshold) {
            createAlert(budget, BudgetAlert.AlertType.RECOVERED, threshold);
        }
    }

    /**
     * Create a budget alert
     */
    private void createAlert(Budget budget, BudgetAlert.AlertType type, Integer percentage) {
        BudgetAlert alert = BudgetAlert.builder()
                .budget(budget)
                .alertType(type)
                .percentage(percentage)
                .build();
        budgetAlertRepository.save(alert);
    }

    /**
     * Get alerts for a budget
     */
    public List<BudgetAlertResponse> getBudgetAlerts(Long budgetId, Long userId) {
        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        return budgetAlertRepository.findByBudgetIdOrderByTriggeredAtDesc(budgetId).stream()
                .map(this::toBudgetAlertResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get budget status (summary)
     */
    public BudgetResponse getBudgetStatus(Long budgetId, Long userId) {
        return getBudget(budgetId, userId);
    }

    // Helper methods
    private BudgetResponse toBudgetResponse(Budget budget) {
        return BudgetResponse.builder()
                .id(budget.getId())
                .name(budget.getName())
                .category(budget.getCategory())
                .monthlyLimit(budget.getMonthlyLimit())
                .spentAmount(budget.getSpentAmount())
                .percentageSpent(budget.getPercentageSpent())
                .startDate(budget.getStartDate())
                .endDate(budget.getEndDate())
                .alertThreshold(budget.getAlertThreshold())
                .createdAt(budget.getCreatedAt())
                .updatedAt(budget.getUpdatedAt())
                .build();
    }

    private BudgetAlertResponse toBudgetAlertResponse(BudgetAlert alert) {
        return BudgetAlertResponse.builder()
                .id(alert.getId())
                .budgetId(alert.getBudget().getId())
                .alertType(alert.getAlertType().toString())
                .percentage(alert.getPercentage())
                .triggeredAt(alert.getTriggeredAt())
                .build();
    }
}
