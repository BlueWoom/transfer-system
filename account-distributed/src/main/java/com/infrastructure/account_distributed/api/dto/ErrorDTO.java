package com.infrastructure.account_distributed.api.dto;

import com.domain.account.exception.AccountDomainErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
public class ErrorDTO {

    private final String errorCode;

    private final String message;

    private final UUID transactionId;

    private final OffsetDateTime timestamp;

    private final HttpStatus httpStatus;

    public ErrorDTO() {
        this.errorCode = null;
        this.message = null;
        this.transactionId = null;
        this.timestamp = null;
        this.httpStatus = null;
    }

    public ErrorDTO(AccountDomainErrorCode errorCode, String message, UUID transactionId, OffsetDateTime timestamp) {
        this.errorCode = errorCode.getValue();
        this.message = message;
        this.transactionId = transactionId;
        this.timestamp = timestamp;
        this.httpStatus = convertAccountCode(errorCode);
    }

    public ErrorDTO(String message, OffsetDateTime timestamp) {
        this.errorCode = "Unexpected error";
        this.message = message;
        this.transactionId = null;
        this.timestamp = timestamp;
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public static HttpStatus convertAccountCode(AccountDomainErrorCode errorCode) {
        return switch (errorCode) {
            case ACCOUNT_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case INVALID_REQUEST -> HttpStatus.BAD_REQUEST;
        };
    }
}
