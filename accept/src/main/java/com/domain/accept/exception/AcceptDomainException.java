package com.domain.accept.exception;

import lombok.Getter;

@Getter
public class AcceptDomainException extends RuntimeException {

    private final AcceptDomainErrorCode errorCode;

    private final String message;

    public AcceptDomainException(AcceptDomainErrorCode errorCode, String message) {
        super(String.format("%s: %s", errorCode.getValue(), message));
        this.errorCode = errorCode;
        this.message = message;
    }
}
