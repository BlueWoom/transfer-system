package com.domain.registry.model;

import com.domain.registry.exception.RegistryDomainErrorCode;
import com.domain.registry.exception.RegistryDomainException;
import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
public final class FailedTransfer extends Transfer {

    private final OffsetDateTime processedAt;

    private final RegistryDomainErrorCode errorCode;

    public FailedTransfer(UUID transferId, UUID requestId, OffsetDateTime createdAt, OffsetDateTime processedAt, RegistryDomainErrorCode errorCode) {
        super(transferId, requestId, createdAt);

        validateNotIncomplete(processedAt);

        this.processedAt = processedAt;
        this.errorCode = errorCode;
    }

    private void validateNotIncomplete(OffsetDateTime processedAt) {
        if (processedAt == null) {
            throw new RegistryDomainException(RegistryDomainErrorCode.INVALID_TRANSFER, "Transfer is not processable due to missing fields.");
        }
    }
}