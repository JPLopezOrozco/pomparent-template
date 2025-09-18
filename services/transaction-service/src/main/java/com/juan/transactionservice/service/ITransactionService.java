package com.juan.transactionservice.service;

import com.juan.transactionservice.dto.TransactionRequestDto;
import com.juan.transactionservice.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ITransactionService {
    Transaction save(TransactionRequestDto transaction);
    Transaction getById(Long id);
    Page<Transaction> getBySource(Long sourceId, Pageable pageable);
    Page<Transaction> getByTarget(Long targetId, Pageable pageable);
    Page<Transaction> getBySourceAndTargetId(Long sourceId, Long targetId, Pageable pageable);
    Page<Transaction> getDialog(Long a, Long b, Pageable pageable);
    Transaction approve(Long id);
    Transaction reject(Long id);
}
