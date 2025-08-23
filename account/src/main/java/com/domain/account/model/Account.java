package com.domain.account.model;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record Account(Long ownerId,
                      String currency,
                      BigDecimal balance) { }
