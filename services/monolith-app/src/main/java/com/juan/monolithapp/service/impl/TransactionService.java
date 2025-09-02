package com.juan.monolithapp.service.impl;

import com.juan.monolithapp.dto.TransactionRequestDto;
import com.juan.monolithapp.exception.AccountNotFoundException;
import com.juan.monolithapp.exception.NoTransactionException;
import com.juan.monolithapp.model.Account;
import com.juan.monolithapp.model.Transaction;
import com.juan.monolithapp.model.TransactionType;
import com.juan.monolithapp.repository.AccountRepository;
import com.juan.monolithapp.repository.TransactionRepository;
import com.juan.monolithapp.service.ITransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService implements ITransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public Transaction save(TransactionRequestDto transaction) {
        Account account = accountRepository.findById(transaction.accountId()).orElseThrow(()-> new AccountNotFoundException("Account not found"));

        if (transaction.type() == TransactionType.DEBIT){
            account.debit(transaction.amount());
        }else {
            account.credit(transaction.amount());
        }
        accountRepository.save(account);
        Transaction transactionToSave = Transaction.builder()
                .account(account)
                .type(transaction.type())
                .amount(transaction.amount())
                .currency(account.getCurrency())
                .description(transaction.description())
                .build();
        return transactionRepository.save(transactionToSave);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> getByAccountId(Long id, Pageable pageable) {
        if (accountRepository.existsById(id)) {
            return transactionRepository.findByAccountId(id, pageable);
        }
        throw new AccountNotFoundException("Account not found");
    }

    @Override
    @Transactional(readOnly = true)
    public Transaction getById(Long id) {
        return transactionRepository.findById(id).orElseThrow(()-> new NoTransactionException("No transaction found with id " + id));
    }
}
