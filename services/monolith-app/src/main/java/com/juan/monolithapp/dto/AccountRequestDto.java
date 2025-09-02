package com.juan.monolithapp.dto;

import com.juan.monolithapp.model.CurrencyCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AccountRequestDto(@NotBlank String holderName,@NotNull CurrencyCode currencyCode) {
}
