package com.domain.account.usecase.request;

import java.math.BigDecimal;

public record AccountUpdateRequest(Long ownerId,
                                   BigDecimal balance) { }
