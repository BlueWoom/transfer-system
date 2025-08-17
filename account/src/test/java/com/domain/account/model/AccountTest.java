package com.domain.account.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class AccountTest {

    @Test
    void should_create_account_with_all_arguments() {
        Long ownerId = 1L;
        String currency = "USD";
        BigDecimal balance = new BigDecimal("100.0");

        Account account = new Account(ownerId, currency, balance);

        assertThat(account.ownerId()).isEqualTo(ownerId);
        assertThat(account.currency()).isEqualTo(currency);
        assertThat(account.balance()).isEqualTo(balance);
    }

    @Test
    void should_create_account_with_default_balance() {
        Long ownerId = 2L;
        String currency = "EUR";

        Account account = new Account(ownerId, currency, BigDecimal.ZERO);

        assertThat(account.ownerId()).isEqualTo(ownerId);
        assertThat(account.currency()).isEqualTo(currency);
        assertThat(account.balance()).isEqualTo(BigDecimal.ZERO);
    }
}