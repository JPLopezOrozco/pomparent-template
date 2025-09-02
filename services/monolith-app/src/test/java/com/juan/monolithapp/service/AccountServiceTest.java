package com.juan.monolithapp.service;


import com.juan.monolithapp.dto.AccountRequestDto;
import com.juan.monolithapp.exception.AccountNotFoundException;
import com.juan.monolithapp.model.Account;
import com.juan.monolithapp.model.CurrencyCode;
import com.juan.monolithapp.repository.AccountRepository;
import com.juan.monolithapp.service.impl.AccountService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private JdbcTemplate jdbcTemplate;


    @InjectMocks
    private AccountService accountService;

    @Test
    public void AccountServiceCreate() {
        AccountRequestDto accountRequestDto = AccountRequestDto.builder()
                .holderName("Juan Lopez")
                .currencyCode(CurrencyCode.EUR)
                .build();

        when(jdbcTemplate.queryForObject("SELECT nextval('account_number_seq')", Long.class))
                .thenReturn(5L);

        when(accountRepository.save(Mockito.any(Account.class))).thenAnswer(inv->{
            Account a = inv.getArgument(0);
            a.setId(4L);
            return a;
        });

        Account savedAccount = accountService.createAccount(accountRequestDto);

        Assertions.assertThat(savedAccount).isNotNull();
        Assertions.assertThat(savedAccount.getId()).isEqualTo(4L);
        Assertions.assertThat(savedAccount.getHolderName()).isEqualTo("Juan Lopez");
        Assertions.assertThat(savedAccount.getNumber()).isEqualTo("ACC-00005");
        Assertions.assertThat(savedAccount.getBalance()).isEqualTo(BigDecimal.ZERO);
        Assertions.assertThat(savedAccount.isActive()).isTrue();
        Assertions.assertThat(savedAccount.getCurrency()).isEqualTo(CurrencyCode.EUR);

        Mockito.verify(accountRepository, Mockito.times(1)).save(Mockito.any(Account.class));
        Mockito.verify(jdbcTemplate, Mockito.times(1)).queryForObject("SELECT nextval('account_number_seq')", Long.class);
        Mockito.verifyNoMoreInteractions(jdbcTemplate, accountRepository);

    }

    @Test
    public void AccountServiceGetAccount() {
       Account account = Account.builder()
               .id(4L)
               .holderName("Juan Lopez")
               .currency(CurrencyCode.EUR)
               .balance(BigDecimal.ZERO)
               .active(true)
               .build();


       when(accountRepository.findById(4L)).thenReturn(Optional.of(account));
       Account accountWanted = accountService.getAccount(account.getId());

       Assertions.assertThat(accountWanted).isNotNull();
       Assertions.assertThat(accountWanted.getId()).isEqualTo(4L);
       Assertions.assertThat(accountWanted.getHolderName()).isEqualTo("Juan Lopez");
       Assertions.assertThat(accountWanted.getCurrency()).isEqualTo(CurrencyCode.EUR);
       Mockito.verify(accountRepository, Mockito.times(1)).findById(4L);
       Mockito.verifyNoMoreInteractions(accountRepository, jdbcTemplate);
    }

    @Test
    public void AccountServiceGetAccountNotFound() {
        when(accountRepository.findById(4L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(()->accountService.getAccount(4L))
                .isInstanceOf(AccountNotFoundException.class);
        Mockito.verify(accountRepository, Mockito.times(1)).findById(4L);
        Mockito.verifyNoMoreInteractions(accountRepository, jdbcTemplate);

    }
    }
