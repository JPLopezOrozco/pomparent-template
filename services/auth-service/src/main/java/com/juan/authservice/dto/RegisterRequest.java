package com.juan.authservice.dto;

import jakarta.validation.constraints.NotNull;

public record RegisterRequest(@NotNull String name,
                              @NotNull String email,
                              @NotNull String password) {
}
