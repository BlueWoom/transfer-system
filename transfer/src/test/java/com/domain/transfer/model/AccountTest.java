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
        // Arrange & Act
        Account account = new Account(1L, Currency.EUR, new BigDecimal("100.0"));

        // Assert
        assertThat(account.getOwnerId()).isEqualTo(1L);
        assertThat(account.getCurrency()).isEqualTo(Currency.EUR);
        assertThat(account.getBalance()).isEqualTo(new BigDecimal("100.0"));
    }

    @Test
    void shouldCreateAccountWithZeroBalance() {
        // Arrange & Act
        Account account = new Account(2L, Currency.USD);

        // Assert
        assertThat(account.getOwnerId()).isEqualTo(2L);
        assertThat(account.getCurrency()).isEqualTo(Currency.USD);
        assertThat(account.getBalance()).isZero();
    }

    @Test
    void hasFundShouldReturnTrueWhenBalanceIsGreater() {
        // Arrange
        Account account = new Account(1L, Currency.USD, new BigDecimal("100.0"));

        // Act & Assert
        assertThat(account.hasFund(new BigDecimal("50.0"))).isTrue();
    }

    @Test
    void hasFundShouldReturnTrueWhenBalanceIsEqual() {
        // Arrange
        Account account = new Account(1L, Currency.USD, new BigDecimal("100.0"));

        // Act & Assert
        assertThat(account.hasFund(new BigDecimal("100.0"))).isTrue();
    }

    @Test
    void hasFundShouldReturnFalseWhenBalanceIsLower() {
        // Arrange
        Account account = new Account(1L, Currency.USD, new BigDecimal("100.0"));

        // Act & Assert
        assertThat(account.hasFund(new BigDecimal("150.0"))).isFalse();
    }

    @Test
    void debitShouldReturnNewAccountWithDecreasedBalance() {
        // Arrange
        Account originalAccount = new Account(1L, Currency.USD, new BigDecimal("200.50"));
        BigDecimal amountToDebit = new BigDecimal("50.25");

        // Act
        Account newAccount = originalAccount.debit(amountToDebit);

        // Assert
        assertThat(newAccount.getBalance()).isEqualTo(new BigDecimal("150.25"));
        // Verify immutability: original account balance should not change
        assertThat(originalAccount.getBalance()).isEqualTo(new BigDecimal("200.50"));
    }

    @Test
    void debitShouldThrowException_whenInsufficientFunds() {
        // Arrange
        Account account = new Account(1L, Currency.USD, new BigDecimal("100.0"));
        BigDecimal amountToDebit = new BigDecimal("150.0");

        // Act & Assert
        assertThatThrownBy(() -> account.debit(amountToDebit))
                .isInstanceOf(TransferDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", TransferDomainErrorCode.INSUFFICIENT_BALANCE)
                .hasMessage("Insufficient funds for debit operation.");
    }

    @Test
    void creditShouldReturnNewAccountWithIncreasedBalance() {
        // Arrange
        Account originalAccount = new Account(1L, Currency.EUR, new BigDecimal("100.0"));
        BigDecimal amountToCredit = new BigDecimal("50.0");

        // Act
        Account newAccount = originalAccount.credit(amountToCredit);

        // Assert
        assertThat(newAccount.getBalance()).isEqualTo(new BigDecimal("150.0"));
        // Verify immutability: original account balance should not change
        assertThat(originalAccount.getBalance()).isEqualTo(new BigDecimal("100.0"));
    }
}