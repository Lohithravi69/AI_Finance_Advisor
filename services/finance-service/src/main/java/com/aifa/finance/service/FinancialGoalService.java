package com.aifa.finance.service;

import com.aifa.finance.domain.FinancialGoal;
import com.aifa.finance.domain.User;
import com.aifa.finance.dto.FinancialGoalRequest;
import com.aifa.finance.dto.FinancialGoalResponse;
import com.aifa.finance.exception.ResourceNotFoundException;
import com.aifa.finance.repository.FinancialGoalRepository;
import com.aifa.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinancialGoalService {

    private final FinancialGoalRepository goalRepository;
    private final UserRepository userRepository;

    @Transactional
    public FinancialGoalResponse createGoal(Long userId, FinancialGoalRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        FinancialGoal goal = FinancialGoal.builder()
            .user(user)
            .goalName(request.goalName())
            .goalCategory(FinancialGoal.GoalCategory.valueOf(request.goalCategory()))
            .description(request.description())
            .targetAmount(request.targetAmount())
            .currentAmount(BigDecimal.ZERO)
            .progressPercentage(BigDecimal.ZERO)
            .startDate(request.startDate())
            .targetDate(request.targetDate())
            .status(FinancialGoal.GoalStatus.NOT_STARTED)
            .priority(FinancialGoal.GoalPriority.valueOf(request.priority()))
            .monthlyContribution(request.monthlyContribution())
            .accountId(request.accountId())
            .isRecurring(request.isRecurring() != null ? request.isRecurring() : false)
            .notes(request.notes())
            .build();

        goal = goalRepository.save(goal);
        return toResponse(goal);
    }

    @Transactional(readOnly = true)
    public List<FinancialGoalResponse> getGoalsByUser(Long userId) {
        return goalRepository.findByUserIdOrderByTargetDateAsc(userId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FinancialGoalResponse> getUpcomingGoals(Long userId) {
        return goalRepository.findUpcomingGoals(userId, LocalDate.now())
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FinancialGoalResponse> getGoalsByStatus(Long userId, String status) {
        return goalRepository.findByUserIdAndStatus(userId, status)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FinancialGoalResponse> getGoalsByCategory(Long userId, String category) {
        return goalRepository.findByUserIdAndGoalCategory(userId, category)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FinancialGoalResponse getGoalById(Long id) {
        return goalRepository.findById(id)
            .map(this::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
    }

    @Transactional
    public FinancialGoalResponse updateGoal(Long id, FinancialGoalRequest request) {
        FinancialGoal goal = goalRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        goal.setGoalName(request.goalName());
        goal.setGoalCategory(FinancialGoal.GoalCategory.valueOf(request.goalCategory()));
        goal.setDescription(request.description());
        goal.setTargetAmount(request.targetAmount());
        goal.setStartDate(request.startDate());
        goal.setTargetDate(request.targetDate());
        goal.setPriority(FinancialGoal.GoalPriority.valueOf(request.priority()));
        goal.setMonthlyContribution(request.monthlyContribution());
        goal.setAccountId(request.accountId());
        goal.setIsRecurring(request.isRecurring());
        goal.setNotes(request.notes());

        goal = goalRepository.save(goal);
        return toResponse(goal);
    }

    @Transactional
    public FinancialGoalResponse updateProgress(Long id, BigDecimal amount) {
        FinancialGoal goal = goalRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        goal.setCurrentAmount(goal.getCurrentAmount().add(amount));
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(FinancialGoal.GoalStatus.COMPLETED);
            goal.setCompletionDate(LocalDate.now());
        }

        goal = goalRepository.save(goal);
        return toResponse(goal);
    }

    @Transactional
    public FinancialGoalResponse updateStatus(Long id, String status) {
        FinancialGoal goal = goalRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        goal.setStatus(FinancialGoal.GoalStatus.valueOf(status));
        goal = goalRepository.save(goal);
        return toResponse(goal);
    }

    @Transactional
    public void deleteGoal(Long id) {
        goalRepository.deleteById(id);
    }

    private FinancialGoalResponse toResponse(FinancialGoal goal) {
        return new FinancialGoalResponse(
            goal.getId(),
            goal.getGoalName(),
            goal.getGoalCategory().name(),
            goal.getDescription(),
            goal.getTargetAmount(),
            goal.getCurrentAmount(),
            goal.getProgressPercentage(),
            goal.getStartDate(),
            goal.getTargetDate(),
            goal.getStatus().name(),
            goal.getPriority().name(),
            goal.getMonthlyContribution(),
            goal.getAccountId(),
            goal.getIsRecurring(),
            goal.getCompletionDate(),
            goal.getDaysRemaining(),
            goal.getNotes(),
            goal.getCreatedAt(),
            goal.getUpdatedAt()
        );
    }
}
