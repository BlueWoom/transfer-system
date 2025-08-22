package com.domain.accept.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AcceptedTransfer(UUID transferId,
                               UUID requestId,
                               OffsetDateTime createdAt,
                               Long originatorId,
                               Long beneficiaryId,
                               BigDecimal amount) { }
