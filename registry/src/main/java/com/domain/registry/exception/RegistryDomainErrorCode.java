package com.domain.registry.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public enum RegistryDomainErrorCode {

    NEGATIVE_AMOUNT("Negative amount"),
    INSUFFICIENT_BALANCE("Insufficient balance"),

    DUPLICATED_REQUEST("Duplicated request"),

    ACCOUNT_NOT_FOUND("Account not found"),
    TRANSFER_NOT_FOUND("Transfer not found"),
    EXCHANGE_RATE_NOT_FOUND("Exchange rate not found"),

    INVALID_BENEFICIARY("Invalid beneficiary"),
    INVALID_EXCHANGE_RATE("Invalid exchange rate"),
    INVALID_TRANSFER("Transfer is invalid"),
    INVALID_CURRENCY("Invalid currency"),

    EXCHANGE_RATE_NEGATIVE("Exchange rate is negative"),
    UNEXPECTED_ERROR("Unexpected error");

    private final String value;
}
