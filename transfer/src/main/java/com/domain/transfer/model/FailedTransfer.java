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
public final class FailedTransfer extends Transfer {

    private final OffsetDateTime processedAt;

    public FailedTransfer(UUID transferId, UUID requestId, OffsetDateTime createdAt, BigDecimal transferAmount, Account originator, Account beneficiary, OffsetDateTime processedAt) {
        super(transferId, requestId, createdAt, transferAmount, originator, beneficiary);

        validate(processedAt);

        this.processedAt = processedAt;
    }

    public FailedTransfer(PendingTransfer pendingTransfer, OffsetDateTime processedAt) {
        super(pendingTransfer.getTransferId(), pendingTransfer.getRequestId(), pendingTransfer.getCreatedAt(), pendingTransfer.getTransferAmount(), pendingTransfer.getOriginator(), pendingTransfer.getBeneficiary());

        validate(processedAt);

        this.processedAt = processedAt;
    }

    public FailedTransfer(UUID transferId, UUID requestId, OffsetDateTime createdAt, OffsetDateTime processedAt) {
        super(transferId, requestId, createdAt, null, null, null);

        validate(processedAt);

        this.processedAt = processedAt;
    }

    private void validate(OffsetDateTime processedAt) {
        if (processedAt == null) {
            throw new TransferDomainException(TransferDomainErrorCode.INVALID_TRANSFER, "Transfer is not processable due to missing fields.");
        }
    }
}