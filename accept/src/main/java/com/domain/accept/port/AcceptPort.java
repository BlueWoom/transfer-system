package com.domain.accept.port;

import com.domain.accept.port.query.IdempotencyKey;

import java.util.Optional;
import java.util.UUID;

public interface AcceptPort {

    boolean existsByRequestId(IdempotencyKey checkIdempotency);

    Optional<UUID> getTransferIdByRequestId(UUID uuid);
}
