package com.domain.registry.model;

import com.domain.registry.exception.RegistryDomainErrorCode;
import com.domain.registry.exception.RegistryDomainException;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@ToString
public abstract class Transfer {

    protected final UUID transferId;

    protected final UUID requestId;

    protected final OffsetDateTime createdAt;

    protected Transfer(UUID transferId, UUID requestId, OffsetDateTime createdAt) {
        if (transferId == null || requestId == null || createdAt == null) {
            throw new RegistryDomainException(RegistryDomainErrorCode.INVALID_TRANSFER, "Transfer is invalid due to missing fields.");
        }

        this.transferId = transferId;
        this.requestId = requestId;
        this.createdAt = createdAt;
    }
}