package com.domain.transfer.model;

import com.domain.transfer.exception.TransferDomainErrorCode;
import com.domain.transfer.exception.TransferDomainException;
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

    protected final BigDecimal transferAmount;

    protected final Account originator;

    protected final Account beneficiary;

    protected Transfer(UUID transferId, UUID requestId, OffsetDateTime createdAt, BigDecimal transferAmount, Account originator, Account beneficiary) {
        if (transferId == null || requestId == null || createdAt == null) {
            throw new TransferDomainException(TransferDomainErrorCode.INVALID_TRANSFER, "Transfer is invalid due to missing fields.");
        }

        this.transferId = transferId;
        this.requestId = requestId;
        this.createdAt = createdAt;
        this.transferAmount = transferAmount;
        this.originator = originator;
        this.beneficiary = beneficiary;
    }
}