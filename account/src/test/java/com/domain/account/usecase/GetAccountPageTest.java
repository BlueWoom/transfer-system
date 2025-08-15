package com.domain.account.usecase;

import com.domain.account.model.Account;
import com.domain.account.model.Currency;
import com.domain.account.model.PageResult;
import com.domain.account.port.AccountPort;
import com.domain.account.port.query.AccountPageQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GetAccountPageTest {

    private AccountPort accountPort;

    private GetAccountPage usecase;

    @BeforeEach
    void setUp() {
        accountPort = mock(AccountPort.class);
        usecase = new GetAccountPage(accountPort) {};
    }

    @Test
    void should_return_page_result() {
        AccountPageQuery query = new AccountPageQuery(0, 2);
        List<Account> accounts = List.of(
                new Account(1L, Currency.USD, new BigDecimal("100.0")),
                new Account(2L, Currency.EUR, new BigDecimal("200.0"))
        );
        PageResult<Account> pageResult = new PageResult<>(accounts, 2L, 1);

        when(accountPort.getAllAccounts(query)).thenReturn(pageResult);

        PageResult<Account> result = usecase.execute(query);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getContent().get(0).getOwnerId()).isEqualTo(1L);
        assertThat(result.getContent().get(1).getOwnerId()).isEqualTo(2L);
        verify(accountPort).getAllAccounts(query);
    }
}