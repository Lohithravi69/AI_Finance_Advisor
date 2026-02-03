package com.aifa.finance.service;

import com.aifa.finance.dto.UserProfileDto;
import com.aifa.finance.dto.LoginResponse;
import com.aifa.finance.domain.User;
import com.aifa.finance.model.UserPreferences;
import com.aifa.finance.repository.UserRepository;
import com.aifa.finance.repository.UserPreferencesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final UserPreferencesRepository userPreferencesRepository;

    /**
     * Get or create user from JWT token
     */
    public User getOrCreateUser(Jwt jwt) {
        String keycloakId = jwt.getClaimAsString("sub");
        String email = jwt.getClaimAsString("email");
        String fullName = jwt.getClaimAsString("name");

        return userRepository.findByKeycloakId(keycloakId)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .keycloakId(keycloakId)
                            .email(email)
                            .fullName(fullName)
                            .monthlyIncome(0.0)
                            .createdAt(LocalDateTime.now())
                            .build();
                    User savedUser = userRepository.save(newUser);

                    // Create default preferences
                    UserPreferences preferences = UserPreferences.builder()
                            .user(savedUser)
                            .currencyPreference("USD")
                            .timezone("UTC")
                            .emailNotifications(true)
                            .pushNotifications(true)
                            .budgetAlerts(true)
                            .goalMilestoneNotifications(true)
                            .investmentUpdates(true)
                            .build();
                    userPreferencesRepository.save(preferences);

                    return savedUser;
                });
    }

    /**
     * Get user profile by user ID
     */
    public UserProfileDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserProfileDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .monthlyIncome(user.getMonthlyIncome())
                .build();
    }

    /**
     * Get user profile from JWT token
     */
    public UserProfileDto getUserProfileFromJwt(Jwt jwt) {
        User user = getOrCreateUser(jwt);
        return getUserProfile(user.getId());
    }

    /**
     * Update user profile
     */
    public UserProfileDto updateUserProfile(Long userId, UserProfileDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (dto.getFullName() != null) {
            user.setFullName(dto.getFullName());
        }
        if (dto.getMonthlyIncome() != null) {
            user.setMonthlyIncome(dto.getMonthlyIncome());
        }

        User updatedUser = userRepository.save(user);
        return getUserProfile(updatedUser.getId());
    }

    /**
     * Get user preferences
     */
    public UserPreferences getUserPreferences(Long userId) {
        return userPreferencesRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User preferences not found"));
    }

    /**
     * Update user preferences
     */
    public UserPreferences updateUserPreferences(Long userId, UserPreferences preferences) {
        UserPreferences existing = userPreferencesRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User preferences not found"));

        existing.setCurrencyPreference(preferences.getCurrencyPreference());
        existing.setTimezone(preferences.getTimezone());
        existing.setEmailNotifications(preferences.getEmailNotifications());
        existing.setPushNotifications(preferences.getPushNotifications());
        existing.setBudgetAlerts(preferences.getBudgetAlerts());
        existing.setGoalMilestoneNotifications(preferences.getGoalMilestoneNotifications());
        existing.setInvestmentUpdates(preferences.getInvestmentUpdates());

        return userPreferencesRepository.save(existing);
    }

    /**
     * Create login response from JWT
     */
    public LoginResponse createLoginResponse(Jwt jwt) {
        User user = getOrCreateUser(jwt);

        return LoginResponse.builder()
                .token(jwt.getTokenValue())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .userId(user.getId())
                .build();
    }
}
