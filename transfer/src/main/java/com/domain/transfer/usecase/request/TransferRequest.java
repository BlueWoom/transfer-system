package com.domain.transfer.usecase.request;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest(UUID requestId, Long originatorId, Long beneficiaryId, BigDecimal amount) { }
