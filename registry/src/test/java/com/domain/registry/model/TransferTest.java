package com.domain.registry.model;

import com.domain.registry.exception.RegistryDomainErrorCode;
import com.domain.registry.exception.RegistryDomainException;
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

    private ConcreteTransfer createValidTransfer() {
        return new ConcreteTransfer(
                UUID.randomUUID(),
                UUID.randomUUID(),
                OffsetDateTime.now()
        );
    }

    @Test
    void shouldCreateTransferWithValidArguments() {
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        BigDecimal transferAmount = BigDecimal.valueOf(500.00);
        Account originator = mock(Account.class);
        Account beneficiary = mock(Account.class);
        ConcreteTransfer transfer = new ConcreteTransfer(transferId, requestId, createdAt);

        assertNotNull(transfer);
        assertEquals(transferId, transfer.getTransferId());
        assertEquals(requestId, transfer.getRequestId());
        assertEquals(createdAt, transfer.getCreatedAt());
    }

    @Test
    void shouldThrowExceptionWhenTransferIdIsNull() {
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        RegistryDomainException exception = assertThrows(RegistryDomainException.class, () ->
                new ConcreteTransfer(null, requestId, createdAt)
        );

        assertEquals(RegistryDomainErrorCode.INVALID_TRANSFER, exception.getErrorCode());
        assertEquals("Transfer is invalid due to missing fields.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenRequestIdIsNull() {
        UUID transferId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        RegistryDomainException exception = assertThrows(RegistryDomainException.class, () ->
                new ConcreteTransfer(transferId, null, createdAt)
        );

        assertEquals(RegistryDomainErrorCode.INVALID_TRANSFER, exception.getErrorCode());
        assertEquals("Transfer is invalid due to missing fields.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCreatedAtIsNull() {
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();

        RegistryDomainException exception = assertThrows(RegistryDomainException.class, () ->
                new ConcreteTransfer(transferId, requestId, null)
        );

        assertEquals(RegistryDomainErrorCode.INVALID_TRANSFER, exception.getErrorCode());
        assertEquals("Transfer is invalid due to missing fields.", exception.getMessage());
    }

    @Test
    void shouldHaveCorrectStringRepresentation() {
        ConcreteTransfer transfer = createValidTransfer();
        String expectedStart = "ConcreteTransfer(super=Transfer(transferId=" + transfer.getTransferId();
        assertTrue(transfer.toString().startsWith(expectedStart));
        assertTrue(transfer.toString().contains("requestId=" + transfer.getRequestId()));
        assertTrue(transfer.toString().contains("createdAt=" + transfer.getCreatedAt()));
    }
}