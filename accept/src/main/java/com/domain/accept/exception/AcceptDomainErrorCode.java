package com.domain.accept.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AcceptDomainErrorCode {

    DUPLICATED_REQUEST("Duplicated request"),
    TRANSFER_NOT_FOUND("Transfer not found");

    private final String value;
}
