package com.domain.registry.usecase.request;

import com.domain.registry.model.Account;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record ValidateTransferRequest(UUID transferId,
                                      OffsetDateTime createdAt,
                                      Account originator,
                                      Account beneficiary,
                                      BigDecimal amount) { }
