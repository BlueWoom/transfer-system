package com.domain.transfer.usecase;

import com.domain.transfer.model.FailedTransfer;
import com.domain.transfer.model.PendingTransfer;
import com.domain.transfer.port.TransferPort;
import com.domain.transfer.port.query.TransferQuery;
import com.domain.transfer.usecase.request.FailTransferRequest;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class FailTransfer implements Usecase<FailedTransfer, FailTransferRequest> {

    private final TransferPort transferPort;

    @Override
    public FailedTransfer execute(FailTransferRequest request) {
        // Check if pending request exists and fetch it for update or create a new one
        Optional<PendingTransfer> transfer = transferPort.getPendingTransferForUpdate(new TransferQuery(request.requestId()));

        // if transfer not found, create a new one with FAILED status
        if (transfer.isEmpty()) {
            FailedTransfer result = new FailedTransfer(UUID.randomUUID(), request.requestId(), OffsetDateTime.now(), OffsetDateTime.now());
            transferPort.save(result);
            return result;
        }

        PendingTransfer pendingTransfer = transfer.get();

        // Update transfer status to FAILED
        FailedTransfer result = new FailedTransfer(pendingTransfer, OffsetDateTime.now());

        transferPort.save(result);
        return result;
    }
}
