package com.juan.monolithapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UserLoginRequestDto(@NotNull String email,
                                  @NotNull String password) {
}
