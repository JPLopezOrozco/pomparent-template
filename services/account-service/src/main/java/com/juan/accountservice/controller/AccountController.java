package com.juan.accountservice.controller;

import com.juan.accountservice.dto.AccountRequestDto;
import com.juan.accountservice.dto.AccountResponseDto;
import com.juan.accountservice.dto.TransactionRequestDto;
import com.juan.accountservice.model.Account;
import com.juan.accountservice.service.impl.AccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Tag(name = "Accounts", description = "Operaciones de cuentas")
@Validated
@RestController
@RequestMapping(value = "/accounts", produces = "application/json")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;


    @PreAuthorize("hasRole('ADMIN') or @ownership.canReadAccount(#id, #jwt)")
    @GetMapping(value = "{id}")
    public ResponseEntity<AccountResponseDto> getAccount(@PathVariable @Positive Long id, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().body(AccountResponseDto.from(accountService.getAccount(id)));
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<AccountResponseDto> createAccount(@Valid @RequestBody AccountRequestDto account,
                                                            @AuthenticationPrincipal Jwt jwt) {

        Long userId = ((Number) jwt.getClaim("id")).longValue();
        Account newAccount = accountService.createAccount(account, userId);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newAccount.getId())
                .toUri();

        return ResponseEntity.created(location).body(AccountResponseDto.from(newAccount));
    }

    @PutMapping(value = "/transaction", consumes = "application/json")
    public ResponseEntity<String> applyTransaction(@Valid @RequestBody TransactionRequestDto transaction) {
        accountService.applyTransaction(transaction);
        return ResponseEntity.noContent().build();
    }


}
