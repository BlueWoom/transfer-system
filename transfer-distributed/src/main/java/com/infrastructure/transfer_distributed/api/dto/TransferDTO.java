package com.infrastructure.transfer_distributed.api.dto;

import java.time.OffsetDateTime;
import java.util.UUID;


public record TransferDTO(UUID transferId,
                          UUID requestId,
                          OffsetDateTime createdAt) { }
