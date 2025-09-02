package com.juan.monolithapp.repository;

import com.juan.monolithapp.model.Account;
import com.juan.monolithapp.model.CurrencyCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AccountRepositoryTest {

    @Container
    static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void dbProps(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", pg::getJdbcUrl);
        r.add("spring.datasource.username", pg::getUsername);
        r.add("spring.datasource.password", pg::getPassword);
    }

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void accountRepositorySave() {

        Account account = Account.builder()
                .number("ACC-005")
                .holderName("Juan Lopez")
                .active(true)
                .currency(CurrencyCode.USD)
                .balance(BigDecimal.ZERO)
                .build();

        Account savedAccount = accountRepository.save(account);

        Assertions.assertThat(savedAccount).isNotNull();
        Assertions.assertThat(savedAccount.getId()).isGreaterThan(0);

    }

    @Test
    public void accountRepositoryFindAllReturnMoreThanOneAccount() {
        Account account = Account.builder()
                .number("ACC-006")
                .holderName("Juan Lopez")
                .active(true)
                .currency(CurrencyCode.EUR)
                .balance(BigDecimal.valueOf(10000))
                .build();
        accountRepository.save(account);
        Account account2 = Account.builder()
                .number("ACC-007")
                .holderName("Julio Lepsz")
                .active(true)
                .currency(CurrencyCode.ARS)
                .balance(BigDecimal.ZERO)
                .build();
        accountRepository.save(account2);

        List<Account> accounts = accountRepository.findAll();

        Assertions.assertThat(accounts).size().isGreaterThan(1);
        Assertions.assertThat(accounts).size().isEqualTo(5);
    }

    @Test
    public void accountRepositoryUniqueNumber() {
        Account account = Account.builder()
                .number("ACC-003")
                .holderName("Julio")
                .active(true)
                .currency(CurrencyCode.USD)
                .balance(BigDecimal.ZERO)
                .build();
        Assertions.assertThatThrownBy(()-> accountRepository.saveAndFlush(account)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void accountRepositoryNegativeBalance() {
        Account account = Account.builder()
                .number("ACC-004")
                .holderName("Juan Lopez")
                .active(true)
                .currency(CurrencyCode.EUR)
                .balance(BigDecimal.valueOf(-1))
                .build();

        Assertions.assertThatThrownBy(()-> accountRepository.saveAndFlush(account)).isInstanceOf(DataIntegrityViolationException.class);
    }


}
