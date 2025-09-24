package com.juan.accountservice.dto;

import com.juan.accountservice.model.CurrencyCode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record AccountRequestDto(@NotNull CurrencyCode currencyCode,
                                @PositiveOrZero BigDecimal balance) {
}
