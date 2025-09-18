package com.juan.transactionservice.service.impl;

import com.juan.transactionservice.dto.AccountTransaction;
import com.juan.transactionservice.dto.TransactionRequestDto;
import com.juan.transactionservice.exception.*;
import com.juan.transactionservice.dto.Account;
import com.juan.transactionservice.model.Status;
import com.juan.transactionservice.model.Transaction;
import com.juan.transactionservice.repository.TransactionRepository;
import com.juan.transactionservice.service.ITransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService implements ITransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountGateway accountGateway;

    @Override
    @Transactional
    public Transaction save(TransactionRequestDto transaction) {
        Account source = accountGateway.getAccount(transaction.accountIdSource());
        Account target = accountGateway.getAccount(transaction.accountIdTarget());

        if (!source.getCurrencyCode().equals(target.getCurrencyCode()))throw new CurrencyException("Currency code does not match");
        if (transaction.accountIdSource().equals(transaction.accountIdTarget()))throw new AccountException("Source and target account cannot be the same");
        if (source.getBalance().compareTo(transaction.amount()) < 0)throw new NoBalanceException("Insufficient balance on source account");
        if (!source.isActive() || !target.isActive() )throw new AccountException("One of the accounts is not active");

        String idempotencyKey = (transaction.idempotencyKey() == null || transaction.idempotencyKey().isBlank())
                ? UUID.randomUUID().toString()
                : transaction.idempotencyKey();

        BigDecimal normalizedAmount = transaction.amount().setScale(4, RoundingMode.HALF_UP);


        Transaction newTransaction = Transaction.builder()
                .accountIdSource(transaction.accountIdSource())
                .accountIdTarget(transaction.accountIdTarget())
                .amount(normalizedAmount)
                .currency(source.getCurrencyCode())
                .idempotencyKey(idempotencyKey)
                .description(transaction.description())
                .build();

        try {
            return transactionRepository.save(newTransaction);
        }catch (DataIntegrityViolationException e){
            Transaction existingTransaction = transactionRepository.findByIdempotencyKey(idempotencyKey)
                    .orElseThrow(()-> e);
            validateSamePayload(existingTransaction, transaction);
            return existingTransaction;
        }

    }

    private void validateSamePayload(Transaction existingTransaction, TransactionRequestDto transaction) {
        BigDecimal normalizedAmount = transaction.amount().setScale(4, RoundingMode.HALF_UP);

        boolean same = existingTransaction.getAccountIdSource().equals(transaction.accountIdSource())
                && existingTransaction.getAccountIdTarget().equals(transaction.accountIdTarget())
                && existingTransaction.getAmount().compareTo(normalizedAmount) == 0
                && Objects.equals(existingTransaction.getDescription(), transaction.description());
        if (!same) {
            throw new IdempotencyKeyException("Mismatched idempotency key different payload");
        }
    }


    @Override
    @Transactional(readOnly = true)
    public Transaction getById(Long id) {
        return transactionRepository.findById(id).orElseThrow(()-> new TransactionNotFoundException("Transaction not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> getBySource(Long sourceId, Pageable pageable) {
        return transactionRepository.findByAccountIdSource(sourceId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> getByTarget(Long targetId, Pageable pageable) {
        return transactionRepository.findByAccountIdTarget(targetId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> getBySourceAndTargetId(Long sourceId, Long targetId, Pageable pageable) {
        return transactionRepository.findByAccountIdSourceAndAccountIdTarget(sourceId, targetId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> getDialog(Long a, Long b, Pageable pageable) {
        return transactionRepository.findDialog(a, b, pageable);
    }

    @Override
    @Transactional
    public Transaction approve(Long id) {
        int updated = transactionRepository.approveIfPending(id);

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(()-> new TransactionNotFoundException("Transaction not found"));
        if (updated == 1){
            AccountTransaction accountTransaction = new AccountTransaction(
                    transaction.getAccountIdSource(),
                    transaction.getAccountIdTarget(),
                    transaction.getId(),
                    transaction.getAmount(),
                    transaction.getCurrency()
            );
            accountGateway.processTransaction(accountTransaction);
            return transaction;
        }
        if (transaction.getStatus() == Status.APPROVED) return transaction;
        if (transaction.getStatus() == Status.REJECTED) throw new TransactionException("Transaction is rejected");
        throw new TransactionException("Concurrent state change retry");
    }

    @Override
    @Transactional
    public Transaction reject(Long id){
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(()-> new TransactionNotFoundException("Transaction not found"));

        if (transaction.getStatus() != Status.PENDING) throw new TransactionException("Transaction is not PENDING");
        transaction.setStatus(Status.REJECTED);
        return transactionRepository.save(transaction);
    }


}
