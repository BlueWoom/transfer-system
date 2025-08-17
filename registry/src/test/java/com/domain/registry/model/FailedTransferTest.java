package com.domain.registry.model;

import com.domain.registry.exception.RegistryDomainErrorCode;
import com.domain.registry.exception.RegistryDomainException;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FailedTransferTest {
    @Test
    void shouldCreateFailedTransferWithMinimalDetailsSuccessfully() {
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now().minusHours(1);
        OffsetDateTime processedAt = OffsetDateTime.now();
        RegistryDomainErrorCode errorCode = RegistryDomainErrorCode.ACCOUNT_NOT_FOUND;

        FailedTransfer failedTransfer = new FailedTransfer(transferId, requestId, createdAt, processedAt, errorCode);

        assertNotNull(failedTransfer);
        assertEquals(transferId, failedTransfer.getTransferId());
        assertEquals(requestId, failedTransfer.getRequestId());
        assertEquals(createdAt, failedTransfer.getCreatedAt());
        assertEquals(processedAt, failedTransfer.getProcessedAt());
        assertEquals(errorCode, failedTransfer.getErrorCode());
    }

    @Test
    void shouldThrowExceptionWhenProcessedAtIsNullForFullConstructor() {
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        RegistryDomainErrorCode errorCode = RegistryDomainErrorCode.ACCOUNT_NOT_FOUND;

        RegistryDomainException exception = assertThrows(RegistryDomainException.class, () ->
                new FailedTransfer(transferId, requestId, createdAt, null, errorCode)
        );

        assertEquals(RegistryDomainErrorCode.INVALID_TRANSFER, exception.getErrorCode());
        assertEquals("Transfer is not processable due to missing fields.", exception.getMessage());
    }
}