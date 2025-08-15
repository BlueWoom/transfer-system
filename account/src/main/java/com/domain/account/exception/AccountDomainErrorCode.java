package com.domain.account.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountDomainErrorCode {

    ACCOUNT_NOT_FOUND("Account not found"),
    INVALID_REQUEST("Invalid request");

    private final String value;
}
