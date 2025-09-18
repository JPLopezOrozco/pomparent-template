package com.juan.accountservice.dto;

import com.juan.accountservice.model.CurrencyCode;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AccountRequestDto(@NotNull String holderName,@NotNull CurrencyCode currencyCode, BigDecimal balance) {
}
