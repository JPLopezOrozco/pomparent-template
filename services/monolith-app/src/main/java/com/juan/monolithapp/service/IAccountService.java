package com.juan.monolithapp.service;

import com.juan.monolithapp.dto.AccountRequestDto;
import com.juan.monolithapp.model.Account;

public interface IAccountService {
    Account createAccount(AccountRequestDto account);
    Account getAccount(Long id);
}
