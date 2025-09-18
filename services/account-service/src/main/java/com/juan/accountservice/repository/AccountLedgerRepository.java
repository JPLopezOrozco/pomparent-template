package com.juan.accountservice.repository;

import com.juan.accountservice.model.AccountLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface AccountLedgerRepository extends JpaRepository<AccountLedger, Long> {
    @Modifying
    @Query(value = """
        INSERT INTO account_ledger (account_id, transaction_id, amount, currency, type)
        VALUES (:accountId, :transactionId, :amount, :currency, :type)
        ON CONFLICT (transaction_id, account_id, type) DO NOTHING
    """, nativeQuery = true)
    int insertIfAbsent(@Param("accountId") Long account_id,
                       @Param("transactionId") Long transaction_id,
                       @Param("amount") BigDecimal amount,
                       @Param("currency") String currency,
                       @Param("type") String type
                       );
}
