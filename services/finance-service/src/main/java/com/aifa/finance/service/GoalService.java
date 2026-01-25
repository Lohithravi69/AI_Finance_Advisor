package com.aifa.finance.service;

import com.aifa.finance.api.dto.GoalDto;
import com.aifa.finance.domain.Goal;
import com.aifa.finance.domain.User;
import com.aifa.finance.dto.GoalRequest;
import com.aifa.finance.dto.GoalResponse;
import com.aifa.finance.repository.GoalRepository;
import com.aifa.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public List<GoalDto> listGoals(Jwt jwt) {
        var user = ensureUser(jwt);
        return goalRepository.findByUserAndStatus(user, Goal.GoalStatus.ACTIVE)
            .stream()
            .map(this::toDto)
            .toList();
    }

    public List<GoalResponse> listGoals(String userId) {
        User user = ensureUserByString(userId);
        return goalRepository.findByUser(user)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public GoalDto createGoal(Jwt jwt, GoalDto dto) {
        var user = ensureUser(jwt);
        var goal = new Goal();
        goal.setUser(user);
        goal.setTitle(dto.name());
        goal.setTargetAmount(dto.targetAmount());
        goal.setCurrentAmount(dto.currentAmount());
        goal.setStatus(Goal.GoalStatus.ACTIVE);
        goal = goalRepository.save(goal);
        return toDto(goal);
    }

    public GoalResponse createGoal(GoalRequest request, String userId) {
        User user = ensureUserByString(userId);
        Goal goal = new Goal();
        goal.setUser(user);
        goal.setTitle(request.getTitle());
        goal.setDescription(request.getDescription());
        goal.setTargetAmount(request.getTargetAmount());
        goal.setCurrentAmount(request.getCurrentAmount() != null ? request.getCurrentAmount() : 0.0);
        goal.setStatus(Goal.GoalStatus.ACTIVE);
        goal.setDeadline(request.getDeadline());
        
        Goal saved = goalRepository.save(goal);
        return toResponse(saved);
    }

    public GoalDto updateGoal(Jwt jwt, Long id, GoalDto dto) {
        var user = ensureUser(jwt);
        var goal = goalRepository.findById(id).orElseThrow();
        if (!goal.getUser().equals(user)) throw new RuntimeException("Unauthorized");
        goal.setTitle(dto.name());
        goal.setTargetAmount(dto.targetAmount());
        goal.setCurrentAmount(dto.currentAmount());
        goal = goalRepository.save(goal);
        return toDto(goal);
    }

    public GoalResponse getGoalById(Long id, String userId) {
        User user = ensureUserByString(userId);
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        return toResponse(goal);
    }

    public void deleteGoal(Jwt jwt, Long id) {
        var user = ensureUser(jwt);
        var goal = goalRepository.findById(id).orElseThrow();
        if (!goal.getUser().equals(user)) throw new RuntimeException("Unauthorized");
        goal.setStatus(Goal.GoalStatus.COMPLETED);
        goalRepository.save(goal);
    }

    public void deleteGoal(Long id, String userId) {
        User user = ensureUserByString(userId);
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        goalRepository.delete(goal);
    }

    private User ensureUser(Jwt jwt) {
        var keycloakId = jwt.getSubject();
        return userRepository.findByKeycloakId(keycloakId)
            .orElseGet(() -> {
                var user = new User();
                user.setKeycloakId(keycloakId);
                user.setEmail(jwt.getClaimAsString("email"));
                return userRepository.save(user);
            });
    }

    private User ensureUserByString(String userId) {
        Optional<User> existing = userRepository.findByKeycloakId(userId);
        if (existing.isPresent()) {
            return existing.get();
        }

        User user = new User();
        user.setKeycloakId(userId);
        user.setEmail(userId + "@test.local");
        user.setFullName("Test User");
        user.setMonthlyIncome(0.0);
        return userRepository.save(user);
    }

    private GoalDto toDto(Goal goal) {
        return new GoalDto(
            goal.getId(),
            goal.getTitle(),
            goal.getTargetAmount(),
            goal.getCurrentAmount(),
            goal.getStatus().name()
        );
    }

    private GoalResponse toResponse(Goal goal) {
        return GoalResponse.builder()
                .id(goal.getId())
                .userId(goal.getUser() != null ? goal.getUser().getId() : null)
                .title(goal.getTitle())
                .description(goal.getDescription())
                .targetAmount(goal.getTargetAmount())
                .currentAmount(goal.getCurrentAmount())
                .status(goal.getStatus() != null ? goal.getStatus().name() : "ACTIVE")
                .deadline(goal.getDeadline())
                .createdAt(goal.getCreatedAt())
                .updatedAt(goal.getUpdatedAt())
                .build();
    }
}
