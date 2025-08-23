package com.domain.accept.port.query;

import lombok.Builder;

import java.util.UUID;

@Builder
public record IdempotencyKey(UUID request) { }
