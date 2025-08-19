package com.infrastructure.monolith.usecase.registry;

import com.domain.registry.exception.RegistryDomainErrorCode;
import com.domain.registry.model.FailedTransfer;
import lombok.Getter;

@Getter
public class TransferProcessingException extends RuntimeException {

    private final FailedTransfer failedTransfer;

    private final RegistryDomainErrorCode errorCode;

    public TransferProcessingException(FailedTransfer failedTransfer, RegistryDomainErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.failedTransfer = failedTransfer;
        this.errorCode = errorCode;
    }
}
