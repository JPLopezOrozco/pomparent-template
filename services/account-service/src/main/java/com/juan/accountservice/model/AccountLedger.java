package com.juan.accountservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account_ledger", indexes = {
        @Index(name = "idx_ledger_account_created", columnList = "account_id ,created_at DESC"),
        @Index(name = "idx_ledger_tx", columnList = "transaction_id")
})
public class AccountLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, name = "account_id")
    private Long accountId;
    @Column(nullable = false, name = "transaction_id")
    private Long transactionId;
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private CurrencyCode currency;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 6, name = "type")
    private TransactionType type;
    @CreationTimestamp
    @Column(nullable = false, updatable = false, name = "created_at")
    private Instant createdAt;
}
