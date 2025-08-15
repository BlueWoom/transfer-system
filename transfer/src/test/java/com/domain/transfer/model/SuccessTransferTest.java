package com.domain.transfer.model;

import com.domain.transfer.exception.TransferDomainErrorCode;
import com.domain.transfer.exception.TransferDomainException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SuccessTransferTest {

    private PendingTransfer createValidPendingTransfer() {
        return new PendingTransfer(
                UUID.randomUUID(),
                UUID.randomUUID(),
                OffsetDateTime.now(),
                BigDecimal.valueOf(100.00),
                new Account(1L, Currency.USD, BigDecimal.ZERO),
                new Account(2L, Currency.EUR, BigDecimal.ZERO)
        );
    }

    @Test
    void shouldCreateSuccessTransferWithValidArguments() {
        // Arrange
        PendingTransfer pendingTransfer = createValidPendingTransfer();
        OffsetDateTime processedAt = OffsetDateTime.now();
        BigDecimal exchangeRate = BigDecimal.valueOf(1.2);
        BigDecimal debit = BigDecimal.valueOf(120.00);
        BigDecimal credit = BigDecimal.valueOf(100.00);

        // Act
        SuccessTransfer successTransfer = new SuccessTransfer(pendingTransfer, processedAt, exchangeRate, debit, credit);

        // Assert
        assertNotNull(successTransfer);
        assertEquals(processedAt, successTransfer.getProcessedAt());
        assertEquals(exchangeRate, successTransfer.getExchangeRate());
        assertEquals(debit, successTransfer.getDebit());
        assertEquals(credit, successTransfer.getCredit());
        assertEquals(pendingTransfer.getTransferId(), successTransfer.getTransferId());
    }

    @Test
    void shouldThrowExceptionWhenProcessedAtIsNull() {
        // Arrange
        PendingTransfer pendingTransfer = createValidPendingTransfer();
        BigDecimal exchangeRate = BigDecimal.valueOf(1.2);
        BigDecimal debit = BigDecimal.valueOf(120.00);
        BigDecimal credit = BigDecimal.valueOf(100.00);

        // Act & Assert
        TransferDomainException exception = assertThrows(TransferDomainException.class, () ->
                new SuccessTransfer(pendingTransfer, null, exchangeRate, debit, credit)
        );
        assertEquals(TransferDomainErrorCode.INVALID_TRANSFER, exception.getErrorCode());
        assertEquals("Transfer is not processable due to missing fields.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenExchangeRateIsNull() {
        // Arrange
        PendingTransfer pendingTransfer = createValidPendingTransfer();
        OffsetDateTime processedAt = OffsetDateTime.now();
        BigDecimal debit = BigDecimal.valueOf(120.00);
        BigDecimal credit = BigDecimal.valueOf(100.00);

        // Act
        TransferDomainException exception = assertThrows(TransferDomainException.class, () ->
                new SuccessTransfer(pendingTransfer, processedAt, null, debit, credit)
        );

        // Assert
        assertEquals(TransferDomainErrorCode.INVALID_TRANSFER, exception.getErrorCode());
        assertEquals("Transfer is not processable due to missing fields.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCreditIsNull() {
        // Arrange
        PendingTransfer pendingTransfer = createValidPendingTransfer();
        OffsetDateTime processedAt = OffsetDateTime.now();
        BigDecimal exchangeRate = BigDecimal.valueOf(1.2);
        BigDecimal debit = BigDecimal.valueOf(120.00);
        BigDecimal credit = null;

        // Act
        TransferDomainException exception = assertThrows(TransferDomainException.class, () ->
                new SuccessTransfer(pendingTransfer, processedAt, exchangeRate, debit, credit)
        );

        // Assert
        assertEquals(TransferDomainErrorCode.INVALID_TRANSFER, exception.getErrorCode());
        assertEquals("Transfer is not processable due to missing fields.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDebitIsNull() {
        // Arrange
        PendingTransfer pendingTransfer = createValidPendingTransfer();
        OffsetDateTime processedAt = OffsetDateTime.now();
        BigDecimal exchangeRate = BigDecimal.valueOf(1.2);
        BigDecimal debit = null;
        BigDecimal credit = new BigDecimal(100.00);

        // Act
        TransferDomainException exception = assertThrows(TransferDomainException.class, () ->
                new SuccessTransfer(pendingTransfer, processedAt, exchangeRate, debit, credit)
        );

        // Assert
        assertEquals(TransferDomainErrorCode.INVALID_TRANSFER, exception.getErrorCode());
        assertEquals("Transfer is not processable due to missing fields.", exception.getMessage());
    }
}