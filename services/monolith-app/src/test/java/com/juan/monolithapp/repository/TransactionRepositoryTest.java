package com.juan.monolithapp.repository;

import com.juan.monolithapp.model.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TransactionRepositoryTest {

    @Container
    static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", pg::getJdbcUrl);
        r.add("spring.datasource.username", pg::getUsername);
        r.add("spring.datasource.password", pg::getPassword);
    }

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void TransactionRepositorySave(){
        Account account = accountRepository.saveAndFlush(Account.builder()
                .number("ACC-TX-NEG")
                .holderName("Juan")
                .currency(CurrencyCode.USD)
                .build());

        Transaction transaction = Transaction.builder()
                .account(account)
                .type(TransactionType.CREDIT)
                .amount(BigDecimal.valueOf(1000))
                .currency(account.getCurrency())
                .status(TransactionStatus.APPROVED)
                .build();

        Assertions.assertThat(transactionRepository.save(transaction).getId()).isNotNull();
    }

    @Test
    public void TransactionRepositoryAmountNegative(){
        Account account = accountRepository.saveAndFlush(Account.builder()
                .number("ACC-TX-NEG")
                .holderName("Juan")
                .currency(CurrencyCode.USD)
                .build());

        Transaction transaction = Transaction.builder()
                .account(account)
                .type(TransactionType.CREDIT)
                .amount(BigDecimal.valueOf(-1000))
                .currency(account.getCurrency())
                .status(TransactionStatus.APPROVED)
                .build();

        Assertions.assertThatThrownBy(() -> transactionRepository.saveAndFlush(transaction))
                .isInstanceOf(DataIntegrityViolationException.class);

    }

    @Test
    public void TransactionRepositoryFakeAccount(){
        Account account = Account.builder()
                .id(9999L)
                .number("ACC-TX-NEG")
                .holderName("Juan")
                .currency(CurrencyCode.USD)
                .build();

        Transaction transaction = Transaction.builder()
                .account(account)
                .type(TransactionType.CREDIT)
                .amount(BigDecimal.valueOf(-1000))
                .currency(account.getCurrency())
                .status(TransactionStatus.APPROVED)
                .build();

        Assertions.assertThatThrownBy(() -> transactionRepository.saveAndFlush(transaction))
                .isInstanceOf(DataIntegrityViolationException.class);

    }
    @Test
    public void TransactionRepositoryPagination() throws Exception {

        Account account = accountRepository.saveAndFlush(Account.builder()
                .number("ACC-TX-NEG")
                .holderName("Juan")
                .currency(CurrencyCode.USD)
                .build());

        for (int i = 0; i < 5; i++) {

            Transaction transaction = Transaction.builder()
                    .account(account)
                    .type(TransactionType.CREDIT)
                    .amount(BigDecimal.valueOf(1000))
                    .currency(account.getCurrency())
                    .status(TransactionStatus.APPROVED)
                    .build();
            transactionRepository.saveAndFlush(transaction);
            transactionRepository.flush();
            Thread.sleep(5);
        }

        var page0 = transactionRepository.findByAccountId(account.getId(), PageRequest.of(0,2));

        assertThat(page0.getTotalElements()).isEqualTo(5);
        assertThat(page0.getTotalPages()).isEqualTo(3);
        assertThat(page0.getNumber()).isEqualTo(0);
        assertThat(page0.getSize()).isEqualTo(2);
        assertThat(page0.isFirst()).isTrue();
        assertThat(page0.isLast()).isFalse();
        assertThat(page0.getNumberOfElements()).isEqualTo(2);

        var c0 = page0.getContent();
        assertThat(c0.get(0).getCreatedAt()).isAfter(c0.get(1).getCreatedAt());

        var page1 = transactionRepository.findByAccountId(
                account.getId(), PageRequest.of(1, 2));
        assertThat(page1.getNumberOfElements()).isEqualTo(2);
        assertThat(page1.isFirst()).isFalse();
        assertThat(page1.isLast()).isFalse();

        var page2 = transactionRepository.findByAccountId(
                account.getId(), PageRequest.of(2, 2));
        assertThat(page2.getNumberOfElements()).isEqualTo(1);
        assertThat(page2.isLast()).isTrue();
    }




}
