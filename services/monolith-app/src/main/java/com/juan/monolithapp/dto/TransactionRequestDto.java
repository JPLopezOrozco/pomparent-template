package com.juan.monolithapp.dto;

import com.juan.monolithapp.model.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TransactionRequestDto(@NotNull Long accountId,
                                    @NotNull TransactionType type,
                                    @NotNull @DecimalMin("0.1") BigDecimal amount,
                                    @Size(max = 255) String description) {
}
