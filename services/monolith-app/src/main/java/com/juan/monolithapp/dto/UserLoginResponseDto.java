package com.juan.monolithapp.dto;

import lombok.Builder;

@Builder
public record UserLoginResponseDto(String token, String refreshToken) {
}
