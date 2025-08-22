package com.domain.accept.usecase.request;

import java.math.BigDecimal;
import java.util.UUID;

public record AcceptTransferRequest(UUID requestId,
                                    Long originatorId,
                                    Long beneficiaryId,
                                    BigDecimal amount) { }
