package com.domain.registry.usecase;

import com.domain.registry.model.FailedTransfer;
import com.domain.registry.usecase.request.FailTransferRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;

@Slf4j
@RequiredArgsConstructor
public abstract class FailTransfer implements Usecase<FailedTransfer, FailTransferRequest> {

    @Override
    public FailedTransfer execute(FailTransferRequest request) {
        // Could have more complex logic to handle failed transfer
        return new FailedTransfer(request.transferId(), OffsetDateTime.now(), OffsetDateTime.now(), request.errorCode());
    }
}
