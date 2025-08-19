package com.infrastructure.account_distributed.api;

import com.domain.account.exception.AccountDomainException;
import com.infrastructure.account_distributed.api.dto.ErrorDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountDomainException.class)
    public ResponseEntity<ErrorDTO> handleTransferProcessingException(AccountDomainException ex) {
        ErrorDTO errorDTO = new ErrorDTO(ex.getErrorCode(), ex.getMessage(), null, OffsetDateTime.now());
        return new ResponseEntity<>(errorDTO, errorDTO.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGenericException(Exception ex) {
        ErrorDTO errorDTO = new ErrorDTO(ex.getMessage(), OffsetDateTime.now());
        return new ResponseEntity<>(errorDTO, errorDTO.getHttpStatus());
    }
}
