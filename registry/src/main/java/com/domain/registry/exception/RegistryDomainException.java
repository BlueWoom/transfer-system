package com.domain.registry.exception;

import lombok.Getter;

@Getter
public class RegistryDomainException extends RuntimeException {

    private final RegistryDomainErrorCode errorCode;

    private final String message;

    public RegistryDomainException(RegistryDomainErrorCode errorCode, String message) {
        super(String.format("%s: %s", errorCode.getValue(), message));
        this.errorCode = errorCode;
        this.message = message;
    }
}
