package com.juan.accountservice.service.impl;

import com.juan.accountservice.dto.AccountRequestDto;
import com.juan.accountservice.dto.TransactionRequestDto;
import com.juan.accountservice.exception.AccountException;
import com.juan.accountservice.exception.NoAccountException;
import com.juan.accountservice.model.Account;
import com.juan.accountservice.repository.AccountLedgerRepository;
import com.juan.accountservice.repository.AccountRepository;
import com.juan.accountservice.service.IAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class AccountService implements IAccountService {

    private final AccountRepository accountRepository;
    private final JdbcTemplate jdbcTemplate;
    private final AccountLedgerRepository accountLedgerRepository;

    @Override
    @Transactional(readOnly = true)
    public Account getAccount(Long id) {
        return accountRepository.findById(id).orElseThrow(() -> new NoAccountException("Account not found"));
    }

    @Override
    @Transactional
    public Account createAccount(AccountRequestDto account) {
        Long next = jdbcTemplate.queryForObject("select nextval('account_number_sq')", Long.class);
        String number = String.format("ACC-%08d", next);

        BigDecimal balance = (account.balance() != null) ? account.balance() : BigDecimal.ZERO;

        Account newAccount = Account.builder()
                .holderName(account.holderName())
                .number(number)
                .currency(account.currencyCode())
                .balance(balance)
                .build();

        return accountRepository.save(newAccount);
    }

    @Override
    @Transactional
    public void transaction(TransactionRequestDto transaction) {
        Long first = (transaction.sourceId() < transaction.targetId())
                ? transaction.sourceId() : transaction.targetId();
        Long second = (transaction.sourceId() < transaction.targetId())
                ? transaction.targetId() : transaction.sourceId();
        accountRepository.lockForUpdate(first);
        if (!second.equals(first)) accountRepository.lockForUpdate(second);

        BigDecimal amt = transaction.amount().setScale(4, RoundingMode.HALF_UP);

        int debit = accountLedgerRepository.insertIfAbsent(
                transaction.sourceId(),
                transaction.transactionId(),
                amt,
                transaction.currencyCode().name(),
                "DEBIT"
        );
        if (debit == 1){
            int ok = accountRepository.debit(transaction.sourceId(), transaction.amount(), transaction.currencyCode());
            if (ok != 1)throw new AccountException("Debit failed");
        }

        int credit = accountLedgerRepository.insertIfAbsent(
                transaction.targetId(),
                transaction.transactionId(),
                amt,
                transaction.currencyCode().name(),
                "CREDIT"
        );

        if (credit == 1){
          accountRepository.credit(transaction.targetId(), transaction.amount(), transaction.currencyCode());
        }
    }
}
