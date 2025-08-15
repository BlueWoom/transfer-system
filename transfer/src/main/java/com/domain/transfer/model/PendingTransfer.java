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
public final class PendingTransfer extends Transfer {

    public PendingTransfer(UUID transferId, UUID requestId, OffsetDateTime createdAt, BigDecimal transferAmount, Account originator, Account beneficiary) {
        super(transferId, requestId, createdAt, transferAmount, originator, beneficiary);

        if (transferAmount == null || originator == null || beneficiary == null) {
            throw new TransferDomainException(TransferDomainErrorCode.INVALID_TRANSFER, "Transfer is not processable due to missing fields.");
        }

        if (originator.getOwnerId().equals(beneficiary.getOwnerId())) {
            throw new TransferDomainException(TransferDomainErrorCode.INVALID_BENEFICIARY, "Originator and beneficiary cannot be the same.");
        }

        if (transferAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransferDomainException(TransferDomainErrorCode.INVALID_TRANSFER, "Transfer amount must be greater than zero.");
        }
    }
}