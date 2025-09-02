package com.juan.monolithapp.dto;

import com.juan.monolithapp.model.Account;
import com.juan.monolithapp.model.CurrencyCode;

import java.math.BigDecimal;

public record AccountResponseDto(
        Long id,
        String number,
        String holderName,
        boolean isActive,
        CurrencyCode currency,
        BigDecimal balance) {

    public static AccountResponseDto from(Account account){
        return new AccountResponseDto(
                account.getId(),
                account.getNumber(),
                account.getHolderName(),
                account.isActive(),
                account.getCurrency(),
                account.getBalance());
    }
}
