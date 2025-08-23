package com.domain.account.usecase.request;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AccountUpdateRequest(Long ownerId,
                                   BigDecimal balance) { }
