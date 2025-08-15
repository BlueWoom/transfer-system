package com.domain.account.usecase;

import com.domain.account.exception.AccountDomainErrorCode;
import com.domain.account.exception.AccountDomainException;
import com.domain.account.model.Account;
import com.domain.account.model.Currency;
import com.domain.account.port.AccountPort;
import com.domain.account.port.query.AccountQuery;
import com.domain.account.usecase.request.AccountRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class GetAccountUsecaseTest {

    private AccountPort accountPort;

    private GetAccount usecase;

    @BeforeEach
    void setUp() {
        accountPort = mock(AccountPort.class);
        usecase = new GetAccount(accountPort) {
        };
    }

    @Test
    void should_return_account_when_found() {
        Long ownerId = 42L;
        Account account = new Account(ownerId, Currency.USD, new BigDecimal("100.0"));
        when(accountPort.getAccount(new AccountQuery(ownerId))).thenReturn(Optional.of(account));

        AccountRequest request = new AccountRequest(ownerId);
        Account result = usecase.execute(request);

        assertThat(result.getOwnerId()).isEqualTo(account.getOwnerId());
        assertThat(result.getCurrency()).isEqualTo(account.getCurrency());
        assertThat(result.getBalance()).isEqualTo(account.getBalance());
    }

    @Test
    void should_throw_when_account_not_found() {
        Long ownerId = 99L;
        when(accountPort.getAccount(new AccountQuery(ownerId))).thenReturn(Optional.empty());

        AccountRequest request = new AccountRequest(ownerId);

        assertThatThrownBy(() -> usecase.execute(request))
                .isInstanceOf(AccountDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", AccountDomainErrorCode.ACCOUNT_NOT_FOUND);
    }
}