package com.juan.transactionservice.repository;

import com.juan.transactionservice.config.FeignConfiguration;
import com.juan.transactionservice.dto.Account;
import com.juan.transactionservice.dto.AccountTransaction;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "account-service", configuration = FeignConfiguration.class)
public interface AccountClient {

    @GetMapping("/accounts/{id}")
    Account getAccount(@PathVariable Long id);

    @PutMapping("/accounts/transaction")
    void applyTransaction(@RequestBody AccountTransaction accountTransaction);

}
