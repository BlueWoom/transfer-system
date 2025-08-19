package com.infrastructure.transfer_distributed.queue.message;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TransferRequestMessage(UUID transferId,
                                     UUID requestId,
                                     OffsetDateTime createdAt,
                                     Long originatorId,
                                     Long beneficiaryId,
                                     BigDecimal amount) {
}
