package com.infrastructure.monolith.usecase.accept;

import com.domain.accept.exception.AcceptDomainErrorCode;
import com.domain.accept.model.RejectedTransfer;
import lombok.Getter;

@Getter
public class AcceptTransferException extends RuntimeException {

    private final RejectedTransfer rejectedTransfer;

    private final AcceptDomainErrorCode errorCode;

    public AcceptTransferException(RejectedTransfer rejectedTransfer, AcceptDomainErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.rejectedTransfer = rejectedTransfer;
        this.errorCode = errorCode;
    }
}
