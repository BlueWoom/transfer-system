package com.domain.accept.usecase.request;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record AcceptTransferRequest(UUID requestId,
                                    Long originatorId,
                                    Long beneficiaryId,
                                    BigDecimal amount) { }
