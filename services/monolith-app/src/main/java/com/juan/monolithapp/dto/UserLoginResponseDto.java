package com.juan.monolithapp.dto;

import com.juan.monolithapp.model.User;
import lombok.Builder;

@Builder
public record UserLoginResponseDto(String token, String refreshToken) {
}
