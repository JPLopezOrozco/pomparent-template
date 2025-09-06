package com.juan.monolithapp.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UserRegisterRequestDto(@NotNull String name,
                                     @NotNull String email,
                                     @NotNull String password) {

}
