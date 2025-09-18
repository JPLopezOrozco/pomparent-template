package com.juan.transactionservice.dto;

import com.juan.transactionservice.model.Status;
import com.juan.transactionservice.model.Transaction;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponseDto(
        Long id,
        Long sourceId,
        Long targetId,
        BigDecimal amount,
        String description,
        Status status,
        Instant createdAt
) {
    public static TransactionResponseDto from(Transaction transaction) {
        return new TransactionResponseDto(
                transaction.getId(),
                transaction.getAccountIdSource(),
                transaction.getAccountIdTarget(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getStatus(),
                transaction.getCreatedAt()
        );
    }
}
