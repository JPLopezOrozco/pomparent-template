package com.juan.accountservice.dto;

import com.juan.accountservice.model.CurrencyCode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransactionRequestDto(@NotNull Long sourceId,
                                    @NotNull Long targetId,
                                    @NotNull Long transactionId,
                                    @NotNull @Positive BigDecimal amount,
                                    @NotNull CurrencyCode currencyCode
) {
}
