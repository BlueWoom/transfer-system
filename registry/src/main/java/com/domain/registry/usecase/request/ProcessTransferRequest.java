package com.domain.registry.usecase.request;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ProcessTransferRequest(UUID transferId,
                                     OffsetDateTime createdAt,
                                     Long originatorId,
                                     Long beneficiaryId,
                                     BigDecimal amount) { }
