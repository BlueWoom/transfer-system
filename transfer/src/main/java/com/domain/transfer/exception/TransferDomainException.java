package com.domain.transfer.exception;

import lombok.Getter;

@Getter
public class TransferDomainException extends RuntimeException {

    private final TransferDomainErrorCode errorCode;

    private final String message;

    public TransferDomainException(TransferDomainErrorCode errorCode, String message) {
        super(String.format("%s: %s", errorCode.getValue(), message));
        this.errorCode = errorCode;
        this.message = message;
    }
}
