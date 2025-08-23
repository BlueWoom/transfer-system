package com.domain.accept.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record AcceptedTransfer(UUID transferId,
                               UUID requestId,
                               OffsetDateTime createdAt,
                               Long originatorId,
                               Long beneficiaryId,
                               BigDecimal amount) { }
