package com.juan.monolithapp.service;

import com.juan.monolithapp.dto.TransactionRequestDto;
import com.juan.monolithapp.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ITransactionService {
    Transaction save(TransactionRequestDto transaction);
    Page<Transaction> getByAccountId(Long id, Pageable pageable);
    Transaction getById(Long id);
    Transaction approve(Long transactionId);
    Transaction reject(Long transactionId);
}
