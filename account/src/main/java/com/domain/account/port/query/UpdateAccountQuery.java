package com.domain.account.port.query;

import java.math.BigDecimal;

public record UpdateAccountQuery(Long ownerId,
                                BigDecimal amount) { }
