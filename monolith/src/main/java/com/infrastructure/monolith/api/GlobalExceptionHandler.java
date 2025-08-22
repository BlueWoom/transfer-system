package com.infrastructure.monolith.api;

import com.domain.account.exception.AccountDomainException;
import com.domain.registry.exception.RegistryDomainException;
import com.infrastructure.monolith.api.dto.ErrorDTO;
import com.infrastructure.monolith.api.dto.TransferDTO;
import com.infrastructure.monolith.api.mapper.RegistryMapper;
import com.infrastructure.monolith.usecase.accept.AcceptTransferException;
import com.infrastructure.monolith.usecase.registry.TransferProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TransferProcessingException.class)
    public ResponseEntity<TransferDTO> handleTransferProcessingException(TransferProcessingException ex) {
        return new ResponseEntity<>(RegistryMapper.INSTANCE.mapFromModelToDto(ex.getFailedTransfer()), ErrorDTO.convertRegistryCode(ex.getErrorCode()));
    }

    @ExceptionHandler(AcceptTransferException.class)
    public ResponseEntity<ErrorDTO> handleTransferProcessingException(AcceptTransferException ex) {
        ErrorDTO error = new ErrorDTO(ex.getErrorCode(), ex.getMessage(), ex.getRejectedTransfer().transferId(), ex.getRejectedTransfer().requestId(), OffsetDateTime.now());
        return new ResponseEntity<>(error, error.getHttpStatus());
    }


    @ExceptionHandler(RegistryDomainException.class)
    public ResponseEntity<ErrorDTO> handleTransferProcessingException(RegistryDomainException ex) {
        ErrorDTO errorDTO = new ErrorDTO(ex.getErrorCode(), ex.getMessage(), null, OffsetDateTime.now());
        return new ResponseEntity<>(errorDTO, errorDTO.getHttpStatus());
    }

    @ExceptionHandler(AccountDomainException.class)
    public ResponseEntity<ErrorDTO> handleTransferProcessingException(AccountDomainException ex) {
        ErrorDTO errorDTO = new ErrorDTO(ex.getErrorCode(), ex.getMessage(), null, OffsetDateTime.now());
        return new ResponseEntity<>(errorDTO, errorDTO.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGenericException(Exception ex) {
        log.error(ex.getMessage(), ex);
        ErrorDTO error = new ErrorDTO(ex.getMessage(), OffsetDateTime.now());
        return new ResponseEntity<>(error, error.getHttpStatus());
    }
}
