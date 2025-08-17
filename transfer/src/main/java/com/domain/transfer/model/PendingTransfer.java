package com.domain.transfer.model;

import com.domain.transfer.exception.TransferDomainErrorCode;
import com.domain.transfer.exception.TransferDomainException;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
public final class PendingTransfer {

    private final UUID transferId;

    private final UUID requestId;

    private final OffsetDateTime createdAt;

    private final BigDecimal transferAmount;

    private final Account originator;

    private final Account beneficiary;

    public PendingTransfer(UUID transferId, UUID requestId, OffsetDateTime createdAt, BigDecimal transferAmount, Account originator, Account beneficiary) {
        if (transferId == null || requestId == null || createdAt == null) {
            throw new TransferDomainException(TransferDomainErrorCode.INVALID_TRANSFER, "Transfer is invalid due to missing fields.");
        }

        if (transferAmount == null || originator == null || beneficiary == null) {
            throw new TransferDomainException(TransferDomainErrorCode.INVALID_TRANSFER, "Transfer is not processable due to missing fields.");
        }

        if (originator.ownerId().equals(beneficiary.ownerId())) {
            throw new TransferDomainException(TransferDomainErrorCode.INVALID_BENEFICIARY, "Originator and beneficiary cannot be the same.");
        }

        if (transferAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransferDomainException(TransferDomainErrorCode.NEGATIVE_AMOUNT, "Transfer amount must be greater than zero.");
        }

        this.transferId = transferId;
        this.requestId = requestId;
        this.createdAt = createdAt;
        this.transferAmount = transferAmount;
        this.originator = originator;
        this.beneficiary = beneficiary;
    }
}