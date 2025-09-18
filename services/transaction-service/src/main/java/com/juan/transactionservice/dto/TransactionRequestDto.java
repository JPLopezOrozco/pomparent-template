package com.juan.transactionservice.dto;


import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record TransactionRequestDto(@NotNull Long accountIdSource,
                                    @NotNull Long accountIdTarget,
                                    @NotNull @DecimalMin("0.1") BigDecimal amount,
                                    @Size(max = 255) String description,
                                    @Size(max = 64) String idempotencyKey
                                    ) {
}
