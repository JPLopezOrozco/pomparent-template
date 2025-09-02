package com.juan.monolithapp.controller;

import com.juan.monolithapp.dto.AccountRequestDto;
import com.juan.monolithapp.dto.AccountResponseDto;
import com.juan.monolithapp.model.Account;
import com.juan.monolithapp.service.IAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(value = "/accounts", produces = "application/json")
@RequiredArgsConstructor
public class AccountController {

    private final IAccountService accountService;

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDto> getAccount(@PathVariable("id") Long id) {
        Account account = accountService.getAccount(id);
        return ResponseEntity.ok(AccountResponseDto.from(account));
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<AccountResponseDto> createAccount(@RequestBody @Valid AccountRequestDto account) {
        Account created = accountService.createAccount(account);
        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(AccountResponseDto.from(created));
    }
}
