package com.juan.accountservice.repository;

import com.juan.accountservice.model.Account;
import com.juan.accountservice.model.CurrencyCode;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.id = :id")
    Account lockForUpdate(@Param("id") Long id);

    @Modifying
    @Query("""
    update Account a
        set a.balance = a.balance - :amount
        where a.id = :id and a.balance >= :amount and a.currency = :currency
""")
    int debit(@Param("id") Long id,@Param("amount") BigDecimal amount, @Param("currency") CurrencyCode currencyCode);

    @Modifying
    @Query("""
    update Account a
        set a.balance = a.balance + :amount
        where a.id = :id and a.currency = :currency
""")
    void credit(@Param("id") Long id, @Param("amount") BigDecimal amount,@Param("currency") CurrencyCode currencyCode);

}
