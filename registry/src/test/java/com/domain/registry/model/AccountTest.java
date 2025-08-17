package com.domain.registry.model;

import com.domain.registry.exception.RegistryDomainErrorCode;
import com.domain.registry.exception.RegistryDomainException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void shouldCreateAccountWithInitialBalance() {
        Account account = new Account(1L, Currency.EUR, new BigDecimal("100.0"));
        assertEquals(1L, account.ownerId());
        assertEquals(Currency.EUR, account.currency());
        assertEquals(new BigDecimal("100.0"), account.balance());
    }

    @Test
    void hasFundShouldReturnTrueWhenBalanceIsGreater() {
        Account account = new Account(1L, Currency.USD, new BigDecimal("100.0"));
        assertTrue(account.hasFund(new BigDecimal("50.0")));
    }

    @Test
    void hasFundShouldReturnTrueWhenBalanceIsEqual() {
        Account account = new Account(1L, Currency.USD, new BigDecimal("100.0"));
        assertTrue(account.hasFund(new BigDecimal("100.0")));
    }

    @Test
    void hasFundShouldReturnFalseWhenBalanceIsLower() {
        Account account = new Account(1L, Currency.USD, new BigDecimal("100.0"));
        assertFalse(account.hasFund(new BigDecimal("150.0")));
    }

    @Test
    void debitShouldReturnNewAccountWithDecreasedBalance() {
        Account originalAccount = new Account(1L, Currency.USD, new BigDecimal("200.50"));
        BigDecimal amountToDebit = new BigDecimal("50.25");
        Account newAccount = originalAccount.debit(amountToDebit);
        assertEquals(new BigDecimal("150.25"), newAccount.balance());
        assertEquals(new BigDecimal("200.50"), originalAccount.balance());
    }

    @Test
    void debitShouldThrowException_whenInsufficientFunds() {
        Account account = new Account(1L, Currency.USD, new BigDecimal("100.0"));
        BigDecimal amountToDebit = new BigDecimal("150.0");

        RegistryDomainException exception = assertThrows(RegistryDomainException.class, () -> account.debit(amountToDebit));
        assertEquals(RegistryDomainErrorCode.INSUFFICIENT_BALANCE, exception.getErrorCode());
        assertEquals("Insufficient funds for debit operation.", exception.getMessage());
    }

    @Test
    void creditShouldReturnNewAccountWithIncreasedBalance() {
        Account originalAccount = new Account(1L, Currency.EUR, new BigDecimal("100.0"));
        BigDecimal amountToCredit = new BigDecimal("50.0");
        Account newAccount = originalAccount.credit(amountToCredit);
        assertEquals(new BigDecimal("150.0"), newAccount.balance());
        assertEquals(new BigDecimal("100.0"), originalAccount.balance());
    }
}
