package com.juan.transactionservice.dto;

import com.juan.transactionservice.model.CurrencyCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountTransaction {
    private Long sourceId;
    private Long targetId;
    private Long transactionId;
    private BigDecimal amount;
    private CurrencyCode currencyCode;
}
