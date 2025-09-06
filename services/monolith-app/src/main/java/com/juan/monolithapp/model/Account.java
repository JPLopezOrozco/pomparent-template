package com.juan.monolithapp.model;

import com.juan.monolithapp.exception.InsufficientBalanceException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name="accounts", indexes =@Index(name = "idx_account_number", columnList = "number"))
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 64)
    private String number;
    @Column(nullable = false, length = 128)
    private String holderName;
    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;
    @Column(nullable = false, length = 3)
    @Enumerated(EnumType.STRING)
    private CurrencyCode currency;
    @Builder.Default
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance = BigDecimal.ZERO;
    @Column(updatable = false)
    @CreationTimestamp
    private Instant createdAt;
    @UpdateTimestamp
    private Instant updatedAt;
    @Version
    private Long version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account account)) return false;
        if (this.id == null || account.id == null) return false;
        return Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public void debit(BigDecimal amount) {
        if (this.balance.compareTo(amount) > 0) {
            this.balance = balance.subtract(amount);
        }else{
            throw new InsufficientBalanceException("Amount exceeds current balance");
        }
    }

    public void credit(BigDecimal amount) {
        this.balance = balance.add(amount);
    }
}
