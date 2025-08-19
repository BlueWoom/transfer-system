package com.infrastructure.transfer_distributed.api.dto;

import java.math.BigDecimal;

public record TransferRequestDTO(Long originatorId,
                                 Long beneficiaryId,
                                 BigDecimal amount) { }
