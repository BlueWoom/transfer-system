package com.domain.transfer.model;

import com.domain.transfer.exception.TransferDomainErrorCode;
import com.domain.transfer.exception.TransferDomainException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FailedTransferTest {

    @Test
    void shouldCreateFailedTransferWithAllDetails() {
        // Arrange
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now().minusMinutes(1);
        BigDecimal transferAmount = BigDecimal.valueOf(100.00);
        Account originator = new Account(1L, Currency.EUR, BigDecimal.ZERO);
        Account beneficiary = new Account(2L, Currency.USD, BigDecimal.ZERO);
        OffsetDateTime processedAt = OffsetDateTime.now();

        // Act
        FailedTransfer failedTransfer = new FailedTransfer(
                transferId, requestId, createdAt, transferAmount, originator, beneficiary, processedAt
        );

        // Assert
        assertNotNull(failedTransfer);
        assertEquals(transferId, failedTransfer.getTransferId());
        assertEquals(requestId, failedTransfer.getRequestId());
        assertEquals(createdAt, failedTransfer.getCreatedAt());
        assertEquals(transferAmount, failedTransfer.getTransferAmount());
        assertEquals(originator, failedTransfer.getOriginator());
        assertEquals(beneficiary, failedTransfer.getBeneficiary());
        assertEquals(processedAt, failedTransfer.getProcessedAt());
    }

    @Test
    void shouldThrowExceptionWhenProcessedAtIsNullForFirstConstructor() {
        // Arrange
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now().minusMinutes(1);
        BigDecimal transferAmount = BigDecimal.valueOf(100.00);
        Account originator = new Account(1L, Currency.EUR, BigDecimal.ZERO);
        Account beneficiary = new Account(2L, Currency.USD, BigDecimal.ZERO);

        // Act
        TransferDomainException exception = assertThrows(TransferDomainException.class, () ->
                new FailedTransfer(transferId, requestId, createdAt, transferAmount, originator, beneficiary, null)
        );

        //Assert
        assertEquals(TransferDomainErrorCode.INVALID_TRANSFER, exception.getErrorCode());
        assertEquals("Transfer is not processable due to missing fields.", exception.getMessage());
    }

    @Test
    void shouldCreateFailedTransferFromPendingTransfer() {
        // Arrange
        // Arrange
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now().minusMinutes(1);
        BigDecimal transferAmount = BigDecimal.valueOf(100.00);
        Account originator = new Account(1L, Currency.EUR, BigDecimal.ZERO);
        Account beneficiary = new Account(2L, Currency.USD, BigDecimal.ZERO);
        OffsetDateTime processedAt = OffsetDateTime.now();

        PendingTransfer pendingTransfer = new PendingTransfer(
                transferId, requestId, createdAt, transferAmount, originator, beneficiary
        );

        // Act
        FailedTransfer failedTransfer = new FailedTransfer(pendingTransfer, processedAt);

        // Assert
        assertNotNull(failedTransfer);
        assertEquals(pendingTransfer.getTransferId(), failedTransfer.getTransferId());
        assertEquals(pendingTransfer.getRequestId(), failedTransfer.getRequestId());
        assertEquals(pendingTransfer.getCreatedAt(), failedTransfer.getCreatedAt());
        assertEquals(pendingTransfer.getTransferAmount(), failedTransfer.getTransferAmount());
        assertEquals(pendingTransfer.getOriginator(), failedTransfer.getOriginator());
        assertEquals(pendingTransfer.getBeneficiary(), failedTransfer.getBeneficiary());
        assertEquals(processedAt, failedTransfer.getProcessedAt());
    }

    @Test
    void shouldThrowExceptionWhenProcessedAtIsNullForSecondConstructor() {
        // Arrange
        // Arrange
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now().minusMinutes(1);
        BigDecimal transferAmount = BigDecimal.valueOf(100.00);
        Account originator = new Account(1L, Currency.EUR, BigDecimal.ZERO);
        Account beneficiary = new Account(2L, Currency.USD, BigDecimal.ZERO);

        PendingTransfer pendingTransfer = new PendingTransfer(
                transferId, requestId, createdAt, transferAmount, originator, beneficiary
        );

        // Act & Assert
        TransferDomainException exception = assertThrows(TransferDomainException.class, () ->
                new FailedTransfer(pendingTransfer, null)
        );
        assertEquals(TransferDomainErrorCode.INVALID_TRANSFER, exception.getErrorCode());
        assertEquals("Transfer is not processable due to missing fields.", exception.getMessage());
    }

    @Test
    void shouldCreateFailedTransferWithMinimalDetails() {
        // Arrange
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now().minusMinutes(1);
        OffsetDateTime processedAt = OffsetDateTime.now();

        // Act
        FailedTransfer failedTransfer = new FailedTransfer(
                transferId, requestId, createdAt, processedAt
        );

        // Assert
        assertNotNull(failedTransfer);
        assertEquals(transferId, failedTransfer.getTransferId());
        assertEquals(requestId, failedTransfer.getRequestId());
        assertEquals(createdAt, failedTransfer.getCreatedAt());
        assertNull(failedTransfer.getTransferAmount());
        assertNull(failedTransfer.getOriginator());
        assertNull(failedTransfer.getBeneficiary());
        assertEquals(processedAt, failedTransfer.getProcessedAt());
    }

    @Test
    void shouldThrowExceptionWhenProcessedAtIsNullForThirdConstructor() {
        // Arrange
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now().minusMinutes(1);

        // Act
        TransferDomainException exception = assertThrows(TransferDomainException.class, () ->
                new FailedTransfer(transferId, requestId, createdAt, null)
        );

        // Assert
        assertEquals(TransferDomainErrorCode.INVALID_TRANSFER, exception.getErrorCode());
        assertEquals("Transfer is not processable due to missing fields.", exception.getMessage());
    }
}