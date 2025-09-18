package com.juan.accountservice.dto;

import com.juan.accountservice.model.Account;
import com.juan.accountservice.model.CurrencyCode;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountResponseDto (
        String number,
        String holderName,
        boolean active,
        CurrencyCode currencyCode,
        BigDecimal balance,
        Instant crated,
        Instant updated
) {

    public static AccountResponseDto  from(Account account) {
        return new AccountResponseDto(
                account.getNumber(),
                account.getHolderName(),
                account.isActive(),
                account.getCurrency(),
                account.getBalance(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }
}
