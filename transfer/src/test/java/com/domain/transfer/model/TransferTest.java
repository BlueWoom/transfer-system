package com.domain.transfer.model;

import com.domain.transfer.exception.TransferDomainErrorCode;
import com.domain.transfer.exception.TransferDomainException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class TransferTest {

    // Helper method to create valid test data
    private ConcreteTransfer createValidTransfer() {
        return new ConcreteTransfer(
                UUID.randomUUID(),
                UUID.randomUUID(),
                OffsetDateTime.now(),
                BigDecimal.valueOf(100.50),
                mock(Account.class),
                mock(Account.class)
        );
    }

    @Test
    void shouldCreateTransferWithValidArguments() {
        // Arrange
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        BigDecimal transferAmount = BigDecimal.valueOf(500.00);
        Account originator = mock(Account.class);
        Account beneficiary = mock(Account.class);

        // Act
        ConcreteTransfer transfer = new ConcreteTransfer(
                transferId, requestId, createdAt, transferAmount, originator, beneficiary);

        // Assert
        assertNotNull(transfer);
        assertEquals(transferId, transfer.getTransferId());
        assertEquals(requestId, transfer.getRequestId());
        assertEquals(createdAt, transfer.getCreatedAt());
        assertEquals(transferAmount, transfer.getTransferAmount());
        assertEquals(originator, transfer.getOriginator());
        assertEquals(beneficiary, transfer.getBeneficiary());
    }

    @Test
    void shouldThrowExceptionWhenTransferIdIsNull() {
        // Arrange
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();

        // Act
        TransferDomainException exception = assertThrows(TransferDomainException.class, () ->
                new ConcreteTransfer(null, requestId, createdAt, BigDecimal.TEN, mock(Account.class), mock(Account.class))
        );

        // Assert
        assertEquals(TransferDomainErrorCode.INVALID_TRANSFER, exception.getErrorCode());
        assertEquals("Transfer is invalid due to missing fields.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenRequestIdIsNull() {
        // Arrange
        UUID transferId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();

        // Act
        TransferDomainException exception = assertThrows(TransferDomainException.class, () ->
                new ConcreteTransfer(transferId, null, createdAt, BigDecimal.TEN, mock(Account.class), mock(Account.class))
        );

        // Assert
        assertEquals(TransferDomainErrorCode.INVALID_TRANSFER, exception.getErrorCode());
        assertEquals("Transfer is invalid due to missing fields.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCreatedAtIsNull() {
        // Arrange
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();

        // Act
        TransferDomainException exception = assertThrows(TransferDomainException.class, () ->
                new ConcreteTransfer(transferId, requestId, null, BigDecimal.TEN, mock(Account.class), mock(Account.class))
        );

        // Assert
        assertEquals(TransferDomainErrorCode.INVALID_TRANSFER, exception.getErrorCode());
        assertEquals("Transfer is invalid due to missing fields.", exception.getMessage());
    }

    @Test
    void shouldHaveCorrectStringRepresentation() {
        // Arrange
        ConcreteTransfer transfer = createValidTransfer();
        String expectedStart = "ConcreteTransfer(super=Transfer(transferId=" + transfer.getTransferId();

        // Act & Assert
        assertTrue(transfer.toString().startsWith(expectedStart));
        assertTrue(transfer.toString().contains("requestId=" + transfer.getRequestId()));
        assertTrue(transfer.toString().contains("createdAt=" + transfer.getCreatedAt()));
    }
}