package com.domain.account.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class AccountTest {

    @Test
    void should_create_account_with_all_arguments() {
        Long ownerId = 1L;
        Currency currency = Currency.USD;
        BigDecimal balance = new BigDecimal("100.0");

        Account account = new Account(ownerId, currency, balance);

        assertThat(account.getOwnerId()).isEqualTo(ownerId);
        assertThat(account.getCurrency()).isEqualTo(currency);
        assertThat(account.getBalance()).isEqualTo(balance);
    }

    @Test
    void should_create_account_with_default_balance() {
        Long ownerId = 2L;
        Currency currency = Currency.EUR;

        Account account = new Account(ownerId, currency, BigDecimal.ZERO);

        assertThat(account.getOwnerId()).isEqualTo(ownerId);
        assertThat(account.getCurrency()).isEqualTo(currency);
        assertThat(account.getBalance()).isEqualTo(BigDecimal.ZERO);
    }
}