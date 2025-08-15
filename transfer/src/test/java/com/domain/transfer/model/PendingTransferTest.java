package com.domain.transfer.model;

import com.domain.transfer.exception.TransferDomainErrorCode;
import com.domain.transfer.exception.TransferDomainException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PendingTransferTest {

    @Test
    void shouldCreatePendingTransferSuccessfully() {
        // Arrange
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        BigDecimal transferAmount = BigDecimal.valueOf(100.00);
        Account originator = new Account(1L, Currency.USD, BigDecimal.ZERO);
        Account beneficiary = new Account(2L, Currency.EUR, BigDecimal.ZERO);

        // Act
        PendingTransfer pendingTransfer = new PendingTransfer(transferId, requestId, createdAt, transferAmount, originator, beneficiary);

        // Assert
        assertNotNull(pendingTransfer);
        assertEquals(transferId, pendingTransfer.getTransferId());
        assertEquals(requestId, pendingTransfer.getRequestId());
        assertEquals(createdAt, pendingTransfer.getCreatedAt());
        assertEquals(transferAmount, pendingTransfer.getTransferAmount());
        assertEquals(originator, pendingTransfer.getOriginator());
        assertEquals(beneficiary, pendingTransfer.getBeneficiary());
    }

    @Test
    void shouldThrowExceptionWhenTransferAmountIsNull() {
        // Arrange
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        Account originator = new Account(1L, Currency.USD, BigDecimal.ZERO);
        Account beneficiary = new Account(2L, Currency.EUR, BigDecimal.ZERO);

        // Act
        TransferDomainException exception = assertThrows(TransferDomainException.class, () ->
                new PendingTransfer(transferId, requestId, createdAt, null, originator, beneficiary)
        );

        // Assert
        assertEquals(TransferDomainErrorCode.INVALID_TRANSFER, exception.getErrorCode());
        assertEquals("Transfer is not processable due to missing fields.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenOriginatorIsNull() {
        // Arrange
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        BigDecimal transferAmount = BigDecimal.valueOf(100.00);
        Account beneficiary = new Account(2L, Currency.EUR, BigDecimal.ZERO);

        // Act
        TransferDomainException exception = assertThrows(TransferDomainException.class, () ->
                new PendingTransfer(transferId, requestId, createdAt, transferAmount, null, beneficiary)
        );

        // Assert
        assertEquals(TransferDomainErrorCode.INVALID_TRANSFER, exception.getErrorCode());
        assertEquals("Transfer is not processable due to missing fields.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenBeneficiaryIsNull() {
        // Arrange
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        BigDecimal transferAmount = BigDecimal.valueOf(100.00);
        Account originator = new Account(1L, Currency.USD, BigDecimal.ZERO);

        // Act
        TransferDomainException exception = assertThrows(TransferDomainException.class, () ->
                new PendingTransfer(transferId, requestId, createdAt, transferAmount, originator, null)
        );

        // Assert
        assertEquals(TransferDomainErrorCode.INVALID_TRANSFER, exception.getErrorCode());
        assertEquals("Transfer is not processable due to missing fields.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTransferAmountIsZero() {
        // Arrange
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        BigDecimal transferAmount = BigDecimal.ZERO;
        Account originator = new Account(1L, Currency.USD, BigDecimal.ZERO);
        Account beneficiary = new Account(2L, Currency.EUR, BigDecimal.ZERO);

        // Act
        TransferDomainException exception = assertThrows(TransferDomainException.class, () ->
                new PendingTransfer(transferId, requestId, createdAt, transferAmount, originator, beneficiary)
        );

        // Assert
        assertEquals(TransferDomainErrorCode.INVALID_TRANSFER, exception.getErrorCode());
        assertEquals("Transfer amount must be greater than zero.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTransferAmountIsNegative() {
        // Arrange
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        BigDecimal transferAmount = BigDecimal.valueOf(-50.00);
        Account originator = new Account(1L, Currency.USD, BigDecimal.ZERO);
        Account beneficiary = new Account(2L, Currency.EUR, BigDecimal.ZERO);

        // Act
        TransferDomainException exception = assertThrows(TransferDomainException.class, () ->
                new PendingTransfer(transferId, requestId, createdAt, transferAmount, originator, beneficiary)
        );

        // Assert
        assertEquals(TransferDomainErrorCode.INVALID_TRANSFER, exception.getErrorCode());
        assertEquals("Transfer amount must be greater than zero.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenOriginatorAndBeneficiaryAreTheSame() {
        // Arrange
        Long sameOwnerId = 1L;
        Account sameAccount = new Account(sameOwnerId, Currency.USD, BigDecimal.ZERO);
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        BigDecimal transferAmount = BigDecimal.valueOf(100.00);

        // Act
        TransferDomainException exception = assertThrows(TransferDomainException.class, () ->
                new PendingTransfer(transferId, requestId, createdAt, transferAmount, sameAccount, sameAccount)
        );

        // Assert
        assertEquals(TransferDomainErrorCode.INVALID_BENEFICIARY, exception.getErrorCode());
        assertEquals("Originator and beneficiary cannot be the same.", exception.getMessage());
    }
}