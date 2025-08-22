package com.infrastructure.registry_distributed.queue.message;

import java.math.BigDecimal;

public record AccountUpdateMessage(Long ownerId,
                                   BigDecimal balance) { }
