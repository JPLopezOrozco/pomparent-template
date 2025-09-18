package com.juan.accountservice.service;

import com.juan.accountservice.dto.AccountRequestDto;
import com.juan.accountservice.dto.TransactionRequestDto;
import com.juan.accountservice.model.Account;


public interface IAccountService {
    Account getAccount(Long id);
    Account createAccount(AccountRequestDto account);
    void transaction(TransactionRequestDto transaction);
}
