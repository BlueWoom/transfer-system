package com.domain.accept.usecase;

import com.domain.accept.exception.AcceptDomainErrorCode;
import com.domain.accept.exception.AcceptDomainException;
import com.domain.accept.model.AcceptedTransfer;
import com.domain.accept.port.AcceptPort;
import com.domain.accept.port.query.IdempotencyKey;
import com.domain.accept.usecase.request.AcceptTransferRequest;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class AcceptTransfer implements Usecase<AcceptedTransfer, AcceptTransferRequest> {

    private final AcceptPort acceptPort;

    @Override
    public AcceptedTransfer execute(AcceptTransferRequest request) {
        if(acceptPort.existsByRequestId(new IdempotencyKey(request.requestId()))) {
            throw new AcceptDomainException(AcceptDomainErrorCode.DUPLICATED_REQUEST, String.format("Transfer with requestId %s is duplicated", request.requestId()));
        }

        return AcceptedTransfer.builder()
                .transferId(UUID.randomUUID())
                .requestId(request.requestId())
                .createdAt(OffsetDateTime.now())
                .originatorId(request.originatorId())
                .beneficiaryId(request.beneficiaryId())
                .amount(request.amount())
                .build();
    }
}
