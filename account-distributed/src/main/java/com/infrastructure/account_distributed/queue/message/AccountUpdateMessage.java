package com.infrastructure.account_distributed.queue.message;

import java.math.BigDecimal;

public record AccountUpdateMessage(Long ownerId,
                                   BigDecimal balance) { }
