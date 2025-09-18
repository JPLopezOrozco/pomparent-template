package com.juan.transactionservice.dto;

import com.juan.transactionservice.model.CurrencyCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    private String number;
    private String holderName;
    private boolean active;
    private CurrencyCode currencyCode;
    private BigDecimal balance;
}
