package com.infrastructure.account_distributed.api.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AccountDTO(Long ownerId, String currency, BigDecimal balance) { }
