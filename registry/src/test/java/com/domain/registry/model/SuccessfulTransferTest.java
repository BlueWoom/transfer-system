package com.domain.registry.model;

import com.domain.registry.exception.RegistryDomainErrorCode;
import com.domain.registry.exception.RegistryDomainException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SuccessfulTransferTest {

    @Test
    void shouldCreateSuccessfulTransferSuccessfully() {
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        BigDecimal transferAmount = new BigDecimal("100.00");
        Account originator = new Account(1L, Currency.USD, new BigDecimal("1000.00"));
        Account beneficiary = new Account(2L, Currency.EUR, new BigDecimal("500.00"));
        OffsetDateTime processedAt = OffsetDateTime.now();
        BigDecimal exchangeRate = new BigDecimal("0.9");
        BigDecimal debit = new BigDecimal("90.00");
        BigDecimal credit = new BigDecimal("100.00");

        SuccessfulTransfer transfer = new SuccessfulTransfer(transferId, requestId, createdAt, transferAmount, originator, beneficiary, processedAt, exchangeRate, debit, credit);

        assertNotNull(transfer);
        assertEquals(transferId, transfer.getTransferId());
        assertEquals(requestId, transfer.getRequestId());
        assertEquals(createdAt, transfer.getCreatedAt());
        assertEquals(transferAmount, transfer.getTransferAmount());
        assertEquals(originator, transfer.getOriginator());
        assertEquals(beneficiary, transfer.getBeneficiary());
        assertEquals(processedAt, transfer.getProcessedAt());
        assertEquals(exchangeRate, transfer.getExchangeRate());
        assertEquals(debit, transfer.getDebit());
        assertEquals(credit, transfer.getCredit());
    }

    @Test
    void shouldThrowExceptionWhenTransferAmountIsNull() {
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        Account originator = new Account(1L, Currency.USD, new BigDecimal("1000.00"));
        Account beneficiary = new Account(2L, Currency.EUR, new BigDecimal("500.00"));
        OffsetDateTime processedAt = OffsetDateTime.now();
        BigDecimal exchangeRate = new BigDecimal("0.9");
        BigDecimal debit = new BigDecimal("90.00");
        BigDecimal credit = new BigDecimal("100.00");

        RegistryDomainException exception = assertThrows(RegistryDomainException.class, () ->
                new SuccessfulTransfer(transferId, requestId, createdAt, null, originator, beneficiary, processedAt, exchangeRate, debit, credit)
        );

        assertEquals(RegistryDomainErrorCode.INVALID_TRANSFER, exception.getErrorCode());
    }

    @Test
    void shouldThrowExceptionWhenOriginatorIsNull() {
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        BigDecimal transferAmount = new BigDecimal("100.00");
        Account beneficiary = new Account(2L, Currency.EUR, new BigDecimal("500.00"));
        OffsetDateTime processedAt = OffsetDateTime.now();
        BigDecimal exchangeRate = new BigDecimal("0.9");
        BigDecimal debit = new BigDecimal("90.00");
        BigDecimal credit = new BigDecimal("100.00");

        RegistryDomainException exception = assertThrows(RegistryDomainException.class, () ->
                new SuccessfulTransfer(transferId, requestId, createdAt, transferAmount, null, beneficiary, processedAt, exchangeRate, debit, credit)
        );

        assertEquals(RegistryDomainErrorCode.INVALID_TRANSFER, exception.getErrorCode());
    }

    @Test
    void shouldThrowExceptionWhenBeneficiaryIsNull() {
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        BigDecimal transferAmount = new BigDecimal("100.00");
        Account originator = new Account(1L, Currency.USD, new BigDecimal("1000.00"));
        OffsetDateTime processedAt = OffsetDateTime.now();
        BigDecimal exchangeRate = new BigDecimal("0.9");
        BigDecimal debit = new BigDecimal("90.00");
        BigDecimal credit = new BigDecimal("100.00");

        RegistryDomainException exception = assertThrows(RegistryDomainException.class, () ->
                new SuccessfulTransfer(transferId, requestId, createdAt, transferAmount, originator, null, processedAt, exchangeRate, debit, credit)
        );

        assertEquals(RegistryDomainErrorCode.INVALID_TRANSFER, exception.getErrorCode());
    }

    @Test
    void shouldThrowExceptionWhenOriginatorAndBeneficiaryAreTheSame() {
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        BigDecimal transferAmount = new BigDecimal("100.00");
        Account originator = new Account(2L, Currency.USD, new BigDecimal("1000.00"));
        OffsetDateTime processedAt = OffsetDateTime.now();
        BigDecimal exchangeRate = new BigDecimal("0.9");
        BigDecimal debit = new BigDecimal("90.00");
        BigDecimal credit = new BigDecimal("100.00");

        Account sameBeneficiary = new Account(2L, Currency.EUR, new BigDecimal("500.00"));

        RegistryDomainException exception = assertThrows(RegistryDomainException.class, () ->
                new SuccessfulTransfer(transferId, requestId, createdAt, transferAmount, originator, sameBeneficiary, processedAt, exchangeRate, debit, credit)
        );

        assertEquals(RegistryDomainErrorCode.INVALID_BENEFICIARY, exception.getErrorCode());
    }

    @Test
    void shouldThrowExceptionWhenTransferAmountIsNegative() {
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        Account originator = new Account(1L, Currency.USD, new BigDecimal("1000.00"));
        Account beneficiary = new Account(2L, Currency.EUR, new BigDecimal("500.00"));
        OffsetDateTime processedAt = OffsetDateTime.now();
        BigDecimal exchangeRate = new BigDecimal("0.9");
        BigDecimal debit = new BigDecimal("90.00");
        BigDecimal credit = new BigDecimal("100.00");

        RegistryDomainException exception = assertThrows(RegistryDomainException.class, () ->
                new SuccessfulTransfer(transferId, requestId, createdAt, new BigDecimal("-10.00"), originator, beneficiary, processedAt, exchangeRate, debit, credit)
        );

        assertEquals(RegistryDomainErrorCode.NEGATIVE_AMOUNT, exception.getErrorCode());
    }

    @Test
    void shouldThrowExceptionWhenProcessedAtIsNull() {
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        BigDecimal transferAmount = new BigDecimal("100.00");
        Account originator = new Account(1L, Currency.USD, new BigDecimal("1000.00"));
        Account beneficiary = new Account(2L, Currency.EUR, new BigDecimal("500.00"));
        BigDecimal exchangeRate = new BigDecimal("0.9");
        BigDecimal debit = new BigDecimal("90.00");
        BigDecimal credit = new BigDecimal("100.00");

        RegistryDomainException exception = assertThrows(RegistryDomainException.class, () ->
                new SuccessfulTransfer(transferId, requestId, createdAt, transferAmount, originator, beneficiary, null, exchangeRate, debit, credit)
        );

        assertEquals(RegistryDomainErrorCode.INVALID_TRANSFER, exception.getErrorCode());
    }

    @Test
    void shouldThrowExceptionWhenExchangeRateIsNull() {
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        BigDecimal transferAmount = new BigDecimal("100.00");
        Account originator = new Account(1L, Currency.USD, new BigDecimal("1000.00"));
        Account beneficiary = new Account(2L, Currency.EUR, new BigDecimal("500.00"));
        OffsetDateTime processedAt = OffsetDateTime.now();
        BigDecimal debit = new BigDecimal("90.00");
        BigDecimal credit = new BigDecimal("100.00");

        RegistryDomainException exception = assertThrows(RegistryDomainException.class, () ->
                new SuccessfulTransfer(transferId, requestId, createdAt, transferAmount, originator, beneficiary, processedAt, null, debit, credit)
        );

        assertEquals(RegistryDomainErrorCode.INVALID_TRANSFER, exception.getErrorCode());
    }

    @Test
    void shouldThrowExceptionWhenDebitIsNull() {
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        BigDecimal transferAmount = new BigDecimal("100.00");
        Account originator = new Account(1L, Currency.USD, new BigDecimal("1000.00"));
        Account beneficiary = new Account(2L, Currency.EUR, new BigDecimal("500.00"));
        OffsetDateTime processedAt = OffsetDateTime.now();
        BigDecimal exchangeRate = new BigDecimal("0.9");
        BigDecimal credit = new BigDecimal("100.00");

        RegistryDomainException exception = assertThrows(RegistryDomainException.class, () ->
                new SuccessfulTransfer(transferId, requestId, createdAt, transferAmount, originator, beneficiary, processedAt, exchangeRate, null, credit)
        );

        assertEquals(RegistryDomainErrorCode.INVALID_TRANSFER, exception.getErrorCode());
    }

    @Test
    void shouldThrowExceptionWhenCreditIsNull() {
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        BigDecimal transferAmount = new BigDecimal("100.00");
        Account originator = new Account(1L, Currency.USD, new BigDecimal("1000.00"));
        Account beneficiary = new Account(2L, Currency.EUR, new BigDecimal("500.00"));
        OffsetDateTime processedAt = OffsetDateTime.now();
        BigDecimal exchangeRate = new BigDecimal("0.9");
        BigDecimal debit = new BigDecimal("90.00");

        RegistryDomainException exception = assertThrows(RegistryDomainException.class, () ->
                new SuccessfulTransfer(transferId, requestId, createdAt, transferAmount, originator, beneficiary, processedAt, exchangeRate, debit, null)
        );

        assertEquals(RegistryDomainErrorCode.INVALID_TRANSFER, exception.getErrorCode());
    }
}