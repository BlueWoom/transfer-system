package com.domain.registry.usecase.request;

import com.domain.registry.model.Account;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ValidateTransferRequest(UUID transferId,
                                      UUID requestId,
                                      OffsetDateTime createdAt,
                                      Account originator,
                                      Account beneficiary,
                                      BigDecimal amount) { }
