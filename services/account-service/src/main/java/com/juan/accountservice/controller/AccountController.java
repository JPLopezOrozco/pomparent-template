package com.juan.accountservice.controller;

import com.juan.accountservice.dto.AccountRequestDto;
import com.juan.accountservice.dto.AccountResponseDto;
import com.juan.accountservice.dto.TransactionRequestDto;
import com.juan.accountservice.model.Account;
import com.juan.accountservice.service.impl.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(value = "/accounts", produces = "application/json")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;


    @GetMapping("{id}")
    public ResponseEntity<AccountResponseDto> getAccount(@PathVariable Long id) {
        return ResponseEntity.ok().body(AccountResponseDto.from(accountService.getAccount(id)));
    }


    @PostMapping
    public ResponseEntity<AccountResponseDto> createAccount(@RequestBody AccountRequestDto account) {
        Account newAccount = accountService.createAccount(account);

        var location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/{id}")
                .buildAndExpand(newAccount.getId())
                .toUri();

        return ResponseEntity.created(location).body(AccountResponseDto.from(newAccount));
    }

    @PutMapping("/transaction")
    public ResponseEntity<String> transaction(@Valid @RequestBody TransactionRequestDto transaction) {
        accountService.transaction(transaction);
        return ResponseEntity.ok().body("Transaction successful");
    }


}
