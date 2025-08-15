package com.domain.account.usecase.query;

import com.domain.account.exception.AccountDomainErrorCode;
import com.domain.account.exception.AccountDomainException;
import com.domain.account.port.query.AccountPageQuery;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountPageQueryTest {

    @Test
    void should_create_query_with_valid_arguments() {
        AccountPageQuery query = new AccountPageQuery(0, 10);
        assertThat(query.pageNumber()).isZero();
        assertThat(query.pageSize()).isEqualTo(10);

        AccountPageQuery query2 = new AccountPageQuery(5, 1);
        assertThat(query2.pageNumber()).isEqualTo(5);
        assertThat(query2.pageSize()).isEqualTo(1);
    }

    @Test
    void should_throw_when_page_number_is_negative() {
        assertThatThrownBy(() -> new AccountPageQuery(-1, 10))
                .isInstanceOf(AccountDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", AccountDomainErrorCode.INVALID_REQUEST);
    }

    @Test
    void should_throw_when_page_size_is_zero() {
        assertThatThrownBy(() -> new AccountPageQuery(0, 0))
                .isInstanceOf(AccountDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", AccountDomainErrorCode.INVALID_REQUEST);
    }

    @Test
    void should_throw_when_page_size_is_negative() {
        assertThatThrownBy(() -> new AccountPageQuery(0, -5))
                .isInstanceOf(AccountDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", AccountDomainErrorCode.INVALID_REQUEST);
    }
}