package com.domain.registry.usecase;

import com.domain.registry.model.FailedTransfer;
import com.domain.registry.port.RegistryPort;
import com.domain.registry.usecase.request.FailTransferRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;

@Slf4j
@RequiredArgsConstructor
public abstract class FailTransfer implements Usecase<FailedTransfer, FailTransferRequest> {

    private final RegistryPort registryPort;

    @Override
    public FailedTransfer execute(FailTransferRequest request) {
        FailedTransfer failedTransfer = new FailedTransfer(request.transferId(), request.requestId(), OffsetDateTime.now(), OffsetDateTime.now(), request.errorCode());
        registryPort.createFailedTransfer(failedTransfer);
        log.info("Transfer request {} has FAILED", failedTransfer);
        return failedTransfer;
    }
}
