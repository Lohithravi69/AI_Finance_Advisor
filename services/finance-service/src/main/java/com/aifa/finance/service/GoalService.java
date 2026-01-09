package com.aifa.finance.service;

import com.aifa.finance.api.dto.GoalDto;
import com.aifa.finance.domain.Goal;
import com.aifa.finance.domain.User;
import com.aifa.finance.repository.GoalRepository;
import com.aifa.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public List<GoalDto> listGoals(Jwt jwt) {
        var user = ensureUser(jwt);
        return goalRepository.findByUserAndStatus(user, Goal.Status.ACTIVE)
            .stream()
            .map(this::toDto)
            .toList();
    }

    public GoalDto createGoal(Jwt jwt, GoalDto dto) {
        var user = ensureUser(jwt);
        var goal = new Goal();
        goal.setUser(user);
        goal.setName(dto.name());
        goal.setTargetAmount(dto.targetAmount());
        goal.setCurrentAmount(dto.currentAmount());
        goal.setStatus(Goal.Status.ACTIVE);
        goal = goalRepository.save(goal);
        return toDto(goal);
    }

    public GoalDto updateGoal(Jwt jwt, Long id, GoalDto dto) {
        var user = ensureUser(jwt);
        var goal = goalRepository.findById(id).orElseThrow();
        if (!goal.getUser().equals(user)) throw new RuntimeException("Unauthorized");
        goal.setName(dto.name());
        goal.setTargetAmount(dto.targetAmount());
        goal.setCurrentAmount(dto.currentAmount());
        goal = goalRepository.save(goal);
        return toDto(goal);
    }

    public void deleteGoal(Jwt jwt, Long id) {
        var user = ensureUser(jwt);
        var goal = goalRepository.findById(id).orElseThrow();
        if (!goal.getUser().equals(user)) throw new RuntimeException("Unauthorized");
        goal.setStatus(Goal.Status.COMPLETED);
        goalRepository.save(goal);
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

    private GoalDto toDto(Goal goal) {
        return new GoalDto(
            goal.getId(),
            goal.getName(),
            goal.getTargetAmount(),
            goal.getCurrentAmount(),
            goal.getStatus().name()
        );
    }
}
