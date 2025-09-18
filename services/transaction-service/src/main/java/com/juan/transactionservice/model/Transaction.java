package com.juan.transactionservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transactions",
        indexes = @Index(name = "idx_tx_account_created_at",
                columnList = "account_id_source, account_id_target, created_at" )
)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long accountIdSource;
    @Column(nullable = false)
    private Long accountIdTarget;
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private CurrencyCode currency;
    private String description;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Status status = Status.PENDING;
    @Column(nullable = false, unique = true, length = 64)
    private String idempotencyKey;
    @CreationTimestamp
    private Instant createdAt;
    @Version
    private Long version;
}
