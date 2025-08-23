package com.domain.accept.usecase;

import com.domain.accept.exception.AcceptDomainErrorCode;
import com.domain.accept.exception.AcceptDomainException;
import com.domain.accept.model.RejectedTransfer;
import com.domain.accept.port.AcceptPort;
import com.domain.accept.usecase.request.AcceptTransferRequest;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public abstract class RejectTransfer implements Usecase<RejectedTransfer, AcceptTransferRequest> {

    private final AcceptPort acceptPort;

    @Override
    public RejectedTransfer execute(AcceptTransferRequest request) {
        UUID transferId = acceptPort.getTransferIdByRequestId(request.requestId())
                .orElseThrow(() -> new AcceptDomainException(AcceptDomainErrorCode.TRANSFER_NOT_FOUND, String.format("Transfer with request id %s not found.", request.requestId())));

        return RejectedTransfer.builder()
                .transferId(transferId)
                .requestId(request.requestId())
                .build();
    }
}
