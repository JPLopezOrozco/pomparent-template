package com.juan.accountservice.config;

import com.juan.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Ownership {

    private final AccountRepository accountRepository;

    public boolean canReadAccount(Long accountId, Jwt jwt) {
        Long userId = ((Number) jwt.getClaim("id")).longValue();
        return accountRepository.findById(accountId).map(a->a.getUserId().equals(userId)).orElse(false);
    }

}
