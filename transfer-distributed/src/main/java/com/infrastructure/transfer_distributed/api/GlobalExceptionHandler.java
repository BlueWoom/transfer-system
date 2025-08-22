package com.infrastructure.transfer_distributed.api;

import com.infrastructure.transfer_distributed.api.dto.ErrorDTO;
import com.infrastructure.transfer_distributed.usecase.accept.AcceptTransferException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AcceptTransferException.class)
    public ResponseEntity<ErrorDTO> handleTransferProcessingException(AcceptTransferException ex) {
        ErrorDTO error = new ErrorDTO(ex.getErrorCode(), ex.getMessage(), ex.getRejectedTransfer().transferId(), ex.getRejectedTransfer().requestId(), OffsetDateTime.now());
        return new ResponseEntity<>(error, error.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGenericException(Exception ex) {
        log.error(ex.getMessage(), ex);
        ErrorDTO error = new ErrorDTO(ex.getMessage(), OffsetDateTime.now());
        return new ResponseEntity<>(error, error.getHttpStatus());
    }
}
