package com.juan.transactionservice.service.impl;

import com.juan.transactionservice.dto.Account;
import com.juan.transactionservice.dto.AccountTransaction;
import com.juan.transactionservice.exception.AccountException;
import com.juan.transactionservice.repository.AccountClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountGateway {

    private final AccountClient accountClient;



    @CircuitBreaker(name = "getAccount", fallbackMethod = "accountFallbackMethod")
    public Account getAccount(Long id){
        return accountClient.getAccount(id);
    }

    public Account accountFallbackMethod(Long id, Throwable t) {
        throw new AccountException("Account service not available");
    }

    @CircuitBreaker(name = "processTransaction" ,fallbackMethod = "approveTransactionFallBackMethod")
    public void processTransaction(AccountTransaction accountTransaction) {
        accountClient.transaction(accountTransaction);
    }

    public void approveTransactionFallBackMethod(AccountTransaction accountTransaction, Throwable t) {
        throw new AccountException("Account service not available");
    }


}
