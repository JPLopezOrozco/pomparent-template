package com.juan.accountservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="accounts", indexes = @Index(name = "idx_account_number", columnList = "number"))
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 64)
    private String number;
    @Column(nullable = false, length = 64, name = "user_id")
    private Long userId;
    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private CurrencyCode currency;
    @Builder.Default
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance =  BigDecimal.ZERO;
    @CreationTimestamp
    private Instant createdAt;
    @UpdateTimestamp
    private Instant updatedAt;
    @Version
    private Long version;

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if(!(o instanceof Account account)) return false;
        if(this.id == null || account.id == null) return false;
        return Objects.equals(id, account.id);
    }

}
