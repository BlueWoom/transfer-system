package com.infrastructure.transfer_distributed.api.dto;

import com.domain.accept.exception.AcceptDomainErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
public class ErrorDTO {

    private final String errorCode;

    private final String message;

    private final UUID transactionId;

    private final UUID requestId;

    private final OffsetDateTime timestamp;

    private final HttpStatus httpStatus;

    public ErrorDTO() {
        this.errorCode = null;
        this.message = null;
        this.transactionId = null;
        this.requestId = null;
        this.timestamp = null;
        this.httpStatus = null;
    }

    public ErrorDTO(AcceptDomainErrorCode errorCode, String message, UUID transactionId, UUID requestId, OffsetDateTime timestamp) {
        this.errorCode = errorCode.getValue();
        this.message = message;
        this.transactionId = transactionId;
        this.requestId = requestId;
        this.timestamp = timestamp;
        this.httpStatus = convertAcceptTransferCode(errorCode);
    }

    public ErrorDTO(String message, OffsetDateTime timestamp) {
        this.errorCode = "Unexpected error";
        this.message = message;
        this.transactionId = null;
        this.requestId = null;
        this.timestamp = timestamp;
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public static HttpStatus convertAcceptTransferCode(AcceptDomainErrorCode errorCode) {
        return switch (errorCode) {
            case DUPLICATED_REQUEST ->  HttpStatus.CONFLICT;
            case TRANSFER_NOT_FOUND -> HttpStatus.NOT_FOUND;
        };
    }
}
