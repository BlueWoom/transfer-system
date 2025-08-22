package com.infrastructure.monolith.api.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;


public record TransferDTO(UUID transferId,
                          OffsetDateTime createdAt,
                          BigDecimal transferAmount,
                          AccountDTO originator,
                          AccountDTO beneficiary,
                          TransferStatusDTO status,
                          OffsetDateTime processedAt,
                          BigDecimal exchangeRate,
                          BigDecimal debit,
                          BigDecimal credit,
                          String errorCode) { }
