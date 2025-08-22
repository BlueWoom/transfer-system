package com.infrastructure.transfer_distributed.usecase.accept.adapter;

import com.domain.accept.port.AcceptPort;
import com.domain.accept.port.query.IdempotencyKey;
import com.infrastructure.transfer_distributed.database.entity.RequestEntity;
import com.infrastructure.transfer_distributed.database.repository.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AcceptAdapter implements AcceptPort {

    private final RequestService requestService;

    @Override
    public boolean existsByRequestId(IdempotencyKey key) {
        return requestService.existsByRequestId(key.request());
    }

    @Override
    public Optional<UUID> getTransferIdByRequestId(UUID requestId) {
        return requestService.findByRequestId(requestId)
                .map(RequestEntity::getTransferId);
    }
}
