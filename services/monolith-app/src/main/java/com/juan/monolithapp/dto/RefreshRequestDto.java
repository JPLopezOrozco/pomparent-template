package com.juan.monolithapp.dto;

import jakarta.validation.constraints.NotNull;

public record RefreshRequestDto(@NotNull String refreshToken) {
}
