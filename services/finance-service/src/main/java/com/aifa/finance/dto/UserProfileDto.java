package com.aifa.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private String profilePictureUrl;
    private String currencyPreference;
    private String timezone;
    private Double monthlyIncome;
}
