package com.domain.registry.model;

import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

@ToString(callSuper = true)
public class ConcreteTransfer extends Transfer {

    protected ConcreteTransfer(UUID transferId, OffsetDateTime createdAt) {
        super(transferId, createdAt);
    }
}
