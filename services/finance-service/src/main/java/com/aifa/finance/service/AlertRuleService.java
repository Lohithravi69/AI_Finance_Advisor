package com.aifa.finance.service;

import com.aifa.finance.domain.AlertRule;
import com.aifa.finance.domain.Notification;
import com.aifa.finance.domain.User;
import com.aifa.finance.dto.AlertRuleRequest;
import com.aifa.finance.dto.AlertRuleResponse;
import com.aifa.finance.exception.ResourceNotFoundException;
import com.aifa.finance.repository.AlertRuleRepository;
import com.aifa.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertRuleService {

    private final AlertRuleRepository alertRuleRepository;
    private final UserRepository userRepository;

    @Transactional
    public AlertRuleResponse createAlertRule(Long userId, AlertRuleRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        AlertRule rule = AlertRule.builder()
            .user(user)
            .ruleName(request.ruleName())
            .ruleType(AlertRule.AlertRuleType.valueOf(request.ruleType()))
            .condition(request.condition())
            .thresholdValue(request.thresholdValue())
            .notificationType(Notification.NotificationType.valueOf(request.notificationType()))
            .isEnabled(true)
            .frequency(request.frequency())
            .build();

        rule = alertRuleRepository.save(rule);
        return toResponse(rule);
    }

    @Transactional(readOnly = true)
    public List<AlertRuleResponse> getAlertRulesByUser(Long userId) {
        return alertRuleRepository.findByUserIdOrderByRuleNameAsc(userId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AlertRuleResponse> getEnabledAlertRules(Long userId) {
        return alertRuleRepository.findByUserIdAndIsEnabledTrue(userId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AlertRuleResponse getAlertRuleById(Long id) {
        return alertRuleRepository.findById(id)
            .map(this::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Alert rule not found"));
    }

    @Transactional
    public AlertRuleResponse updateAlertRule(Long id, AlertRuleRequest request) {
        AlertRule rule = alertRuleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Alert rule not found"));

        rule.setRuleName(request.ruleName());
        rule.setRuleType(AlertRule.AlertRuleType.valueOf(request.ruleType()));
        rule.setCondition(request.condition());
        rule.setThresholdValue(request.thresholdValue());
        rule.setNotificationType(Notification.NotificationType.valueOf(request.notificationType()));
        rule.setFrequency(request.frequency());

        rule = alertRuleRepository.save(rule);
        return toResponse(rule);
    }

    @Transactional
    public AlertRuleResponse toggleAlertRule(Long id) {
        AlertRule rule = alertRuleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Alert rule not found"));

        rule.setIsEnabled(!rule.getIsEnabled());
        rule = alertRuleRepository.save(rule);
        return toResponse(rule);
    }

    @Transactional
    public void deleteAlertRule(Long id) {
        alertRuleRepository.deleteById(id);
    }

    private AlertRuleResponse toResponse(AlertRule rule) {
        return new AlertRuleResponse(
            rule.getId(),
            rule.getRuleName(),
            rule.getRuleType().name(),
            rule.getCondition(),
            rule.getThresholdValue(),
            rule.getNotificationType().name(),
            rule.getIsEnabled(),
            rule.getFrequency(),
            rule.getLastTriggered(),
            rule.getCreatedAt(),
            rule.getUpdatedAt()
        );
    }
}
