package com.juan.transactionservice.repository;

import com.juan.transactionservice.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByAccountIdSource(Long accountId, Pageable pageable);
    Page<Transaction> findByAccountIdTarget(Long accountId, Pageable pageable);
    Page<Transaction> findByAccountIdSourceAndAccountIdTarget(Long sourceId, Long targetId, Pageable pageable);
    @Query("""
    select t from Transaction t
    where (t.accountIdSource = :a and t.accountIdTarget = :b)
    or (t.accountIdSource = :b and t.accountIdTarget = :a)
    order by t.createdAt desc
""")
    Page<Transaction> findDialog(Long a, Long b, Pageable pageable);
    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);
    @Modifying
    @Query("""
        update Transaction t
        set t.status = 'APPROVED', t.version = t.version + 1
        where t.id = :id and t.status = 'PENDING'
    """)
    int approveIfPending(@Param("id") Long id);
}

