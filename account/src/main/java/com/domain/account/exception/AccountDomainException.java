package com.domain.account.exception;

public class AccountDomainException extends RuntimeException {

    private final AccountDomainErrorCode errorCode;

    private final String message;

    public AccountDomainException(AccountDomainErrorCode errorCode, String message) {
        super(String.format("%s: %s", errorCode.getValue(), message));
        this.errorCode = errorCode;
        this.message = message;
    }
}
