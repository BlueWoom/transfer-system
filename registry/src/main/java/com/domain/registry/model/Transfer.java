package com.domain.registry.model;

import com.domain.registry.exception.RegistryDomainErrorCode;
import com.domain.registry.exception.RegistryDomainException;
import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@ToString
public abstract class Transfer {

    protected final UUID transferId;

    protected final OffsetDateTime createdAt;

    protected Transfer(UUID transferId, OffsetDateTime createdAt) {
        if (transferId == null || createdAt == null) {
            throw new RegistryDomainException(RegistryDomainErrorCode.INVALID_TRANSFER, "Transfer is invalid due to missing fields.");
        }

        this.transferId = transferId;
        this.createdAt = createdAt;
    }
}