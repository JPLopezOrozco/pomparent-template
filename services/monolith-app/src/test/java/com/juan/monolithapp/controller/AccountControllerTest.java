package com.juan.monolithapp.controller;

import com.juan.monolithapp.dto.AccountRequestDto;
import com.juan.monolithapp.jwt.JwtFilter;
import com.juan.monolithapp.model.Account;
import com.juan.monolithapp.model.CurrencyCode;
import com.juan.monolithapp.service.IAccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.postgresql.hostchooser.HostRequirement.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AccountController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IAccountService accountService;

    @Test
    public void createAccountTest() throws Exception {
        AccountRequestDto acc = new AccountRequestDto("Juan Pedro", CurrencyCode.USD);

        when(accountService.createAccount(any(AccountRequestDto.class))).thenReturn(
                Account.builder()
                        .id(10L)
                        .number("ACC-00009")
                        .holderName(acc.holderName())
                        .active(true)
                        .currency(acc.currencyCode())
                        .balance(BigDecimal.ZERO)
                        .build()
        );
        mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                          { "holderName": "Juan Pedro", "currencyCode": "USD" }
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L));
    }

    @Test
    public void getAccountTest() throws Exception {
        Account acc = Account.builder()
                .id(10L)
                .number("ACC-00009")
                .holderName("Juan Pedro")
                .active(true)
                .currency(CurrencyCode.ARS)
                .balance(BigDecimal.ZERO)
                .build();

        when(accountService.getAccount(any())).thenReturn(acc);

        mockMvc.perform(get("/accounts/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        """
                    {"id": 4,"number": "ACC-00009","holderName": "Juan Pedro","isActive": true,"currency": "ARS","balance": 0
}
                """))
                .andExpect(status().isOk());

    }


}
