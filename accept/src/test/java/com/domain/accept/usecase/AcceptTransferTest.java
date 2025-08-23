package com.domain.accept.usecase;

import com.domain.accept.exception.AcceptDomainErrorCode;
import com.domain.accept.exception.AcceptDomainException;
import com.domain.accept.port.AcceptPort;
import com.domain.accept.port.query.IdempotencyKey;
import com.domain.accept.usecase.request.AcceptTransferRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AcceptTransferTest {

    private AcceptPort acceptPort;

    private AcceptTransfer acceptTransfer;

    @BeforeEach
    void setUp() {
        acceptPort = Mockito.mock(AcceptPort.class);
        acceptTransfer = new AcceptTransfer(acceptPort) { };
    }

    @Test
    @DisplayName("Should not throw exception when request ID is unique")
    void execute_shouldNotThrowException_whenRequestIdIsUnique() {
        UUID requestId = UUID.randomUUID();
        Long originatorId = 1L;
        Long beneficiary = 2L;
        BigDecimal amount = new BigDecimal("100");

        AcceptTransferRequest request = AcceptTransferRequest.builder()
                .requestId(requestId)
                .originatorId(originatorId)
                .beneficiaryId(beneficiary)
                .amount(amount)
                .build();

        when(acceptPort.existsByRequestId(new IdempotencyKey(requestId))).thenReturn(false);
        assertThatCode(() -> acceptTransfer.execute(request)).doesNotThrowAnyException();
        verify(acceptPort).existsByRequestId(new IdempotencyKey(requestId));
    }

    @Test
    @DisplayName("Should throw RegistryDomainException for duplicated request ID")
    void execute_shouldThrowRegistryDomainException_whenRequestIdIsDuplicated() {
        UUID requestId = UUID.randomUUID();
        Long originatorId = 1L;
        Long beneficiary = 2L;
        BigDecimal amount = new BigDecimal("100");

        AcceptTransferRequest request = AcceptTransferRequest.builder()
                .requestId(requestId)
                .originatorId(originatorId)
                .beneficiaryId(beneficiary)
                .amount(amount)
                .build();

        when(acceptPort.existsByRequestId(new IdempotencyKey(requestId))).thenReturn(true);

        assertThatThrownBy(() -> acceptTransfer.execute(request))
                .isInstanceOf(AcceptDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", AcceptDomainErrorCode.DUPLICATED_REQUEST)
                .hasMessage(String.format("Transfer with requestId %s is duplicated", requestId));

        verify(acceptPort).existsByRequestId(new IdempotencyKey(requestId));
    }
}
