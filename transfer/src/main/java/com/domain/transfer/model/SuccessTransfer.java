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
public final class SuccessTransfer extends Transfer {

    private final OffsetDateTime processedAt;

    private final BigDecimal exchangeRate;

    private final BigDecimal debit;

    private final BigDecimal credit;

    public SuccessTransfer(PendingTransfer pendingTransfer, OffsetDateTime processedAt, BigDecimal exchangeRate, BigDecimal debit, BigDecimal credit) {
        super(pendingTransfer.getTransferId(), pendingTransfer.getRequestId(), pendingTransfer.getCreatedAt(), pendingTransfer.getTransferAmount(), pendingTransfer.getOriginator(), pendingTransfer.getBeneficiary());

        if (processedAt == null || exchangeRate == null || debit == null || credit == null) {
            throw new TransferDomainException(TransferDomainErrorCode.INVALID_TRANSFER, "Transfer is not processable due to missing fields.");
        }

        this.processedAt = processedAt;
        this.exchangeRate = exchangeRate;
        this.debit = debit;
        this.credit = credit;
    }
}