package com.domain.transfer.model;

import lombok.ToString;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@ToString(callSuper = true)
public class ConcreteTransfer extends Transfer {
    protected ConcreteTransfer(UUID transferId, UUID requestId, OffsetDateTime createdAt, BigDecimal transferAmount, Account originator, Account beneficiary) {
        super(transferId, requestId, createdAt, transferAmount, originator, beneficiary);
    }
}
