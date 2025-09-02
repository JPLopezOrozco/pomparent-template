package com.juan.monolithapp.service.impl;

import com.juan.monolithapp.dto.AccountRequestDto;
import com.juan.monolithapp.exception.AccountNotFoundException;
import com.juan.monolithapp.model.Account;
import com.juan.monolithapp.repository.AccountRepository;
import com.juan.monolithapp.service.IAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService implements IAccountService {

    private final AccountRepository accountRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Account createAccount(AccountRequestDto account) {
        Long next = jdbcTemplate.queryForObject("SELECT nextval('account_number_seq')", Long.class);
        String number = String.format("ACC-%05d", next);

        Account newAccount = Account.builder()
                .number(number)
                .holderName(account.holderName())
                .currency(account.currencyCode())
                .build();
        return accountRepository.save(newAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public Account getAccount(Long id) {
        return accountRepository.findById(id).orElseThrow(()-> new AccountNotFoundException("No account found with id " + id));
    }
}
