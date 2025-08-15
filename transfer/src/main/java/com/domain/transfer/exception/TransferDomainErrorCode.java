package com.domain.transfer.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransferDomainErrorCode {

    INSUFFICIENT_BALANCE("Insufficient balance"),
    NEGATIVE_AMOUNT("Negative amount"),
    ACCOUNT_NOT_FOUND("Account not found"),
    EXCHANGE_RATE_NOT_FOUND("Exchange rate not found"),
    DUPLICATED_REQUEST("Duplicated request"),
    TRANSFER_NOT_FOUND("Transfer not found"),
    INVALID_BENEFICIARY("Invalid beneficiary"),
    INVALID_EXCHANGE_RATE("Invalid exchange rate"),
    INVALID_TRANSFER("Transfer is invalid");

    private final String value;
}
