package com.juan.monolithapp.dto;

import com.juan.monolithapp.model.Transaction;
import com.juan.monolithapp.model.TransactionStatus;
import com.juan.monolithapp.model.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponseDto(
        Long id,
        Long accountId,
        TransactionType transactionType,
        BigDecimal amount,
        String description,
        TransactionStatus status,
        Instant createdAt,
        BigDecimal balance
) {

    public static TransactionResponseDto from(Transaction transaction) {
        return new TransactionResponseDto(
                transaction.getId(),
                transaction.getAccount().getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getStatus(),
                transaction.getCreatedAt(),
                transaction.getAccount().getBalance()
        );
    }
}
