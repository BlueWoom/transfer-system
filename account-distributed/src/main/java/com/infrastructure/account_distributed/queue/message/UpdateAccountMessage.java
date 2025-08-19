package com.infrastructure.account_distributed.queue.message;

import java.math.BigDecimal;

public record UpdateAccountMessage(Long ownerId,
                                   BigDecimal amount) { }
