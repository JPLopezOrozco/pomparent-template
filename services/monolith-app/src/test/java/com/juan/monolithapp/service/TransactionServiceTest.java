package com.juan.monolithapp.service;

import com.juan.monolithapp.dto.TransactionRequestDto;
import com.juan.monolithapp.exception.InsufficientBalanceException;
import com.juan.monolithapp.model.Account;
import com.juan.monolithapp.model.CurrencyCode;
import com.juan.monolithapp.model.Transaction;
import com.juan.monolithapp.model.TransactionType;
import com.juan.monolithapp.repository.AccountRepository;
import com.juan.monolithapp.repository.TransactionRepository;
import com.juan.monolithapp.service.impl.TransactionService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {


    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    public void TransactionServiceSaveCredit(){

        Account account = Account.builder()
                .id(1L)
                .number("ACC-00001")
                .currency(CurrencyCode.USD)
                .balance(BigDecimal.valueOf(500))
                .active(true)
                .build();

        TransactionRequestDto transactionRequestDto = TransactionRequestDto.builder()
                .accountId(account.getId())
                .type(TransactionType.CREDIT)
                .amount(BigDecimal.valueOf(1000))
                .description("Alquiler")
                .build();
        Transaction transactionSaved = Transaction.builder()
                .id(1000L)
                .account(account)
                .type(TransactionType.CREDIT)
                .amount(BigDecimal.valueOf(1000))
                .currency(CurrencyCode.USD)
                .description("Alquiler")
                .build();

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transactionSaved);

        Transaction tx = transactionService.save(transactionRequestDto);



        Assertions.assertThat(tx).isNotNull();
        Assertions.assertThat(tx.getType()).isEqualTo(TransactionType.CREDIT);
        Assertions.assertThat(tx.getAmount()).isEqualTo(BigDecimal.valueOf(1000));
        Assertions.assertThat(tx.getDescription()).isEqualTo("Alquiler");
        Mockito.verify(accountRepository).findById(1L);
        Mockito.verify(accountRepository).save(any(Account.class));
        Mockito.verify(transactionRepository).save(any(Transaction.class));
        Mockito.verifyNoMoreInteractions(accountRepository, transactionRepository);
    }


    @Test
    public void TransactionServiceSaveDebit(){
        Account account = Account.builder()
                .id(1L)
                .number("ACC-00001")
                .currency(CurrencyCode.USD)
                .balance(BigDecimal.valueOf(5000))
                .active(true)
                .build();

        TransactionRequestDto transactionRequestDto = TransactionRequestDto.builder()
                .accountId(account.getId())
                .type(TransactionType.DEBIT)
                .amount(BigDecimal.valueOf(1000))
                .description("Alquiler")
                .build();
        Transaction transactionSaved = Transaction.builder()
                .id(1000L)
                .account(account)
                .type(TransactionType.DEBIT)
                .amount(BigDecimal.valueOf(1000))
                .currency(CurrencyCode.USD)
                .description("Alquiler")
                .build();

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transactionSaved);

        Transaction tx = transactionService.save(transactionRequestDto);



        Assertions.assertThat(tx).isNotNull();
        Assertions.assertThat(tx.getType()).isEqualTo(TransactionType.DEBIT);
        Assertions.assertThat(tx.getAmount()).isEqualTo(BigDecimal.valueOf(1000));
        Assertions.assertThat(tx.getDescription()).isEqualTo("Alquiler");
        Mockito.verify(accountRepository).findById(1L);
        Mockito.verify(accountRepository).save(any(Account.class));
        Mockito.verify(transactionRepository).save(any(Transaction.class));
        Mockito.verifyNoMoreInteractions(accountRepository, transactionRepository);
    }

    @Test
    public void TransactionServiceSaveDebitNoBalance(){
        Account account = Account.builder()
                .id(1L)
                .number("ACC-00001")
                .currency(CurrencyCode.USD)
                .balance(BigDecimal.valueOf(500))
                .active(true)
                .build();

        TransactionRequestDto transactionRequestDto = TransactionRequestDto.builder()
                .accountId(account.getId())
                .type(TransactionType.DEBIT)
                .amount(BigDecimal.valueOf(1000))
                .description("Alquiler")
                .build();

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));


        Assertions.assertThatThrownBy(() -> transactionService.save(transactionRequestDto))
                .isInstanceOf(InsufficientBalanceException.class);
        Mockito.verify(accountRepository).findById(1L);
        Mockito.verify(transactionRepository, Mockito.never()).save(Mockito.any(Transaction.class));
        Mockito.verify(accountRepository, Mockito.never()).save(Mockito.any(Account.class));
        Mockito.verifyNoMoreInteractions(accountRepository, transactionRepository);
        Assertions.assertThat(account.getBalance()).isEqualByComparingTo("500.00");

    }

    @Test
    public void TransactionServiceGetByAccountId(){
        Account account = Account.builder()
                .id(4L)
                .holderName("Juan Lopez")
                .currency(CurrencyCode.EUR)
                .balance(new BigDecimal("10000"))
                .active(true)
                .build();

        Transaction t1 = Transaction.builder()
                .id(10L).account(account).type(TransactionType.CREDIT)
                .amount(new BigDecimal("1000")).currency(CurrencyCode.EUR)
                .description("Trabajo").build();
        Transaction t2 = Transaction.builder()
                .id(11L).account(account).type(TransactionType.CREDIT)
                .amount(new BigDecimal("1000")).currency(CurrencyCode.EUR)
                .description("Acciones").build();
        Transaction t3 = Transaction.builder()
                .id(12L).account(account).type(TransactionType.DEBIT)
                .amount(new BigDecimal("1000")).currency(CurrencyCode.EUR)
                .description("Alquiler").build();

        Pageable pageable = PageRequest.of(0, 3);
        Page<Transaction> page = new PageImpl<>(List.of(t1, t2, t3), pageable, 3);

        when(accountRepository.existsById(4L)).thenReturn(true);
        when(transactionRepository.findByAccountId(eq(4L), any(Pageable.class)))
                .thenReturn(page);

        Page<Transaction> result = transactionService.getByAccountId(4L, pageable);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getTotalElements()).isEqualTo(3);
        Assertions.assertThat(result.getContent().getFirst().getDescription()).isEqualTo("Trabajo");

        ArgumentCaptor<Pageable> cap = ArgumentCaptor.forClass(Pageable.class);
        Mockito.verify(transactionRepository).findByAccountId(eq(4L), cap.capture());
        Assertions.assertThat(cap.getValue().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(cap.getValue().getPageSize()).isEqualTo(3);

        verifyNoMoreInteractions(transactionRepository);

    }

    @Test
    public void TransactionServiceGetById(){
        Account account = Account.builder()
                .id(4L)
                .holderName("Juan Lopez")
                .currency(CurrencyCode.EUR)
                .balance(new BigDecimal("10000"))
                .active(true)
                .build();

        Transaction t1 = Transaction.builder()
                .id(10L).account(account).type(TransactionType.CREDIT)
                .amount(new BigDecimal("1000")).currency(CurrencyCode.EUR)
                .description("Trabajo").build();


        when(transactionRepository.findById(10L)).thenReturn(Optional.of(t1));

        Transaction transactionWanted = transactionService.getById(10L);

        Assertions.assertThat(transactionWanted).isNotNull();
        Assertions.assertThat(transactionWanted.getDescription()).isEqualTo("Trabajo");
        Mockito.verify(transactionRepository).findById(10L);
        Mockito.verifyNoMoreInteractions(transactionRepository);

    }






}
