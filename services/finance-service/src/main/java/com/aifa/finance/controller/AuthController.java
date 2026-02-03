package com.aifa.finance.controller;

import com.aifa.finance.dto.UserProfileDto;
import com.aifa.finance.model.UserPreferences;
import com.aifa.finance.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * Login endpoint (OAuth2 flow)
     * The token is obtained from Keycloak and sent by the client
     */
    @PostMapping("/login")
    public ResponseEntity<Object> login(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            return ResponseEntity.badRequest()
                    .body(new Object() {
                        public String error = "No valid token provided";
                    });
        }

        Object loginResponse = authService.createLoginResponse(jwt);
        return ResponseEntity.ok(loginResponse);
    }

    /**
     * Get current user profile
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getProfile(@AuthenticationPrincipal Jwt jwt) {
        UserProfileDto profile = authService.getUserProfileFromJwt(jwt);
        return ResponseEntity.ok(profile);
    }

    /**
     * Update current user profile
     */
    @PutMapping("/profile")
    public ResponseEntity<UserProfileDto> updateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody UserProfileDto dto) {
        String keycloakId = jwt.getClaimAsString("sub");
        Long userId = authService.getOrCreateUser(jwt).getId();
        UserProfileDto updatedProfile = authService.updateUserProfile(userId, dto);
        return ResponseEntity.ok(updatedProfile);
    }

    /**
     * Get user preferences
     */
    @GetMapping("/preferences")
    public ResponseEntity<UserPreferences> getPreferences(@AuthenticationPrincipal Jwt jwt) {
        Long userId = authService.getOrCreateUser(jwt).getId();
        UserPreferences preferences = authService.getUserPreferences(userId);
        return ResponseEntity.ok(preferences);
    }

    /**
     * Update user preferences
     */
    @PutMapping("/preferences")
    public ResponseEntity<UserPreferences> updatePreferences(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody UserPreferences preferences) {
        Long userId = authService.getOrCreateUser(jwt).getId();
        UserPreferences updated = authService.updateUserPreferences(userId, preferences);
        return ResponseEntity.ok(updated);
    }

    /**
     * Logout endpoint (optional - client-side mainly handles this)
     */
    @PostMapping("/logout")
    public ResponseEntity<Object> logout(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(new Object() {
            public String message = "Logout successful";
        });
    }
}
