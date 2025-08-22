package com.domain.accept.port.query;

import java.util.UUID;

public record IdempotencyKey(UUID request) { }
