package com.domain.transfer.model;

import com.domain.transfer.exception.TransferDomainErrorCode;
import com.domain.transfer.exception.TransferDomainException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountTest {

    @Test
    void shouldCreateAccountWithInitialBalance() {
        Account account = new Account(1L, Currency.EUR, new BigDecimal("100.0"));

        assertThat(account.ownerId()).isEqualTo(1L);
        assertThat(account.currency()).isEqualTo(Currency.EUR);
        assertThat(account.balance()).isEqualTo(new BigDecimal("100.0"));
    }

    @Test
    void hasFundShouldReturnTrueWhenBalanceIsGreater() {
        Account account = new Account(1L, Currency.USD, new BigDecimal("100.0"));

        assertThat(account.hasFund(new BigDecimal("50.0"))).isTrue();
    }

    @Test
    void hasFundShouldReturnTrueWhenBalanceIsEqual() {
        Account account = new Account(1L, Currency.USD, new BigDecimal("100.0"));

        assertThat(account.hasFund(new BigDecimal("100.0"))).isTrue();
    }

    @Test
    void hasFundShouldReturnFalseWhenBalanceIsLower() {
        Account account = new Account(1L, Currency.USD, new BigDecimal("100.0"));

        assertThat(account.hasFund(new BigDecimal("150.0"))).isFalse();
    }

    @Test
    void debitShouldReturnNewAccountWithDecreasedBalance() {
        Account originalAccount = new Account(1L, Currency.USD, new BigDecimal("200.50"));
        BigDecimal amountToDebit = new BigDecimal("50.25");

        Account newAccount = originalAccount.debit(amountToDebit);

        assertThat(newAccount.balance()).isEqualTo(new BigDecimal("150.25"));
        assertThat(originalAccount.balance()).isEqualTo(new BigDecimal("200.50"));
    }

    @Test
    void debitShouldThrowException_whenInsufficientFunds() {
        Account account = new Account(1L, Currency.USD, new BigDecimal("100.0"));
        BigDecimal amountToDebit = new BigDecimal("150.0");

        assertThatThrownBy(() -> account.debit(amountToDebit))
                .isInstanceOf(TransferDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", TransferDomainErrorCode.INSUFFICIENT_BALANCE)
                .hasMessage("Insufficient funds for debit operation.");
    }

    @Test
    void creditShouldReturnNewAccountWithIncreasedBalance() {
        Account originalAccount = new Account(1L, Currency.EUR, new BigDecimal("100.0"));
        BigDecimal amountToCredit = new BigDecimal("50.0");

        Account newAccount = originalAccount.credit(amountToCredit);

        assertThat(newAccount.balance()).isEqualTo(new BigDecimal("150.0"));
        assertThat(originalAccount.balance()).isEqualTo(new BigDecimal("100.0"));
    }
}