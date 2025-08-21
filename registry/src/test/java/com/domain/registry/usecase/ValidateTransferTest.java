package com.domain.registry.usecase;

import com.domain.registry.exception.RegistryDomainErrorCode;
import com.domain.registry.exception.RegistryDomainException;
import com.domain.registry.model.Account;
import com.domain.registry.model.Currency;
import com.domain.registry.model.SuccessfulTransfer;
import com.domain.registry.port.RegistryPort;
import com.domain.registry.port.query.AccountQuery;
import com.domain.registry.port.query.TransferRequestQuery;
import com.domain.registry.usecase.request.ProcessTransferRequest;
import com.domain.registry.usecase.request.ValidateTransferRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ValidateTransferTest {

    private RegistryPort registryPort;

    private ValidateTransfer validateTransfer;

    @BeforeEach
    void setUp() {
        registryPort = Mockito.mock(RegistryPort.class);
        validateTransfer = new ValidateTransfer(registryPort) { };
    }

    @Test
    void shouldProcessTransferSuccessfully() {
        ValidateTransferRequest request = new ValidateTransferRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                OffsetDateTime.now(),
                new Account(1L, Currency.USD, new BigDecimal("1000.00")),
                new Account(2L, Currency.EUR, new BigDecimal("500.00")),
                BigDecimal.valueOf(100)
        );

        BigDecimal exchangeRate = new BigDecimal("0.85");

        when(registryPort.checkIfRequestExist(any(TransferRequestQuery.class))).thenReturn(false);
        when(registryPort.getExchangeRate(Currency.USD, Currency.EUR)).thenReturn(Optional.of(exchangeRate));

        SuccessfulTransfer result = validateTransfer.execute(request);

        assertThat(result).isNotNull();
        assertThat(result.getTransferId()).isEqualTo(request.transferId());
        assertThat(result.getRequestId()).isEqualTo(request.requestId());
        assertThat(result.getCredit()).isEqualByComparingTo(request.amount());
        assertThat(result.getDebit()).isEqualByComparingTo(request.amount().multiply(exchangeRate));
    }

    @Test
    void shouldThrowExceptionForDuplicatedRequest() {
        ValidateTransferRequest request = new ValidateTransferRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                OffsetDateTime.now(),
                new Account(1L, Currency.USD, new BigDecimal("1000.00")),
                new Account(2L, Currency.EUR, new BigDecimal("500.00")),
                BigDecimal.valueOf(100)
        );

        when(registryPort.checkIfRequestExist(new TransferRequestQuery(request.requestId()))).thenReturn(true);

        assertThatThrownBy(() -> validateTransfer.execute(request))
                .isInstanceOf(RegistryDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", RegistryDomainErrorCode.DUPLICATED_REQUEST);
    }

    @Test
    void shouldThrowExceptionWhenExchangeRateNotFound() {
        ValidateTransferRequest request = new ValidateTransferRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                OffsetDateTime.now(),
                new Account(1L, Currency.USD, new BigDecimal("1000.00")),
                new Account(2L, Currency.EUR, new BigDecimal("500.00")),
                BigDecimal.valueOf(100)
        );


        when(registryPort.checkIfRequestExist(any(TransferRequestQuery.class))).thenReturn(false);

        assertThatThrownBy(() -> validateTransfer.execute(request))
                .isInstanceOf(RegistryDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", RegistryDomainErrorCode.EXCHANGE_RATE_NOT_FOUND);
    }

    @Test
    void shouldThrowExceptionWhenExchangeRateIsNegative() {
        ValidateTransferRequest request = new ValidateTransferRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                OffsetDateTime.now(),
                new Account(1L, Currency.USD, new BigDecimal("1000.00")),
                new Account(2L, Currency.EUR, new BigDecimal("500.00")),
                new BigDecimal("100")
        );

        when(registryPort.checkIfRequestExist(any(TransferRequestQuery.class))).thenReturn(false);
        when(registryPort.getExchangeRate(request.originator().currency(), request.beneficiary().currency())).thenReturn(Optional.of(new BigDecimal("-1")));

        assertThatThrownBy(() -> validateTransfer.execute(request))
                .isInstanceOf(RegistryDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", RegistryDomainErrorCode.EXCHANGE_RATE_NEGATIVE);
    }

    @Test
    void shouldThrowExceptionForInsufficientBalance() {
        ValidateTransferRequest request = new ValidateTransferRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                OffsetDateTime.now(),
                new Account(1L, Currency.USD, new BigDecimal("1.00")),
                new Account(2L, Currency.EUR, new BigDecimal("500.00")),
                new BigDecimal("100")
        );

        BigDecimal exchangeRate = new BigDecimal("0.85");

        when(registryPort.checkIfRequestExist(any(TransferRequestQuery.class))).thenReturn(false);
        when(registryPort.getExchangeRate(request.originator().currency(), request.beneficiary().currency())).thenReturn(Optional.of(exchangeRate));

        assertThatThrownBy(() -> validateTransfer.execute(request))
                .isInstanceOf(RegistryDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", RegistryDomainErrorCode.INSUFFICIENT_BALANCE);
    }

    @Test
    void shouldThrowExceptionWhenOriginatorAndBeneficiaryAreEquals() {
        ValidateTransferRequest request = new ValidateTransferRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                OffsetDateTime.now(),
                new Account(1L, Currency.USD, new BigDecimal("1000.00")),
                new Account(1L, Currency.EUR, new BigDecimal("500.00")),
                new BigDecimal("100")
        );

        BigDecimal exchangeRate = new BigDecimal("1.00");

        when(registryPort.checkIfRequestExist(any(TransferRequestQuery.class))).thenReturn(false);
        when(registryPort.getExchangeRate(request.originator().currency(), request.beneficiary().currency())).thenReturn(Optional.of(exchangeRate));

        assertThatThrownBy(() -> validateTransfer.execute(request))
                .isInstanceOf(RegistryDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", RegistryDomainErrorCode.INVALID_BENEFICIARY);
    }

    @Test
    void shouldThrowExceptionWhenTransferAmountIsNegative() {
        ValidateTransferRequest request = new ValidateTransferRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                OffsetDateTime.now(),
                new Account(1L, Currency.USD, new BigDecimal("1000.00")),
                new Account(2L, Currency.EUR, new BigDecimal("500.00")),
                new BigDecimal("-100")
        );

        BigDecimal exchangeRate = new BigDecimal("0.85");

        when(registryPort.checkIfRequestExist(any(TransferRequestQuery.class))).thenReturn(false);
        when(registryPort.getExchangeRate(request.originator().currency(), request.beneficiary().currency())).thenReturn(Optional.of(exchangeRate));

        assertThatThrownBy(() -> validateTransfer.execute(request))
                .isInstanceOf(RegistryDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", RegistryDomainErrorCode.NEGATIVE_AMOUNT);
    }

    @Test
    void shouldThrowExceptionWhenTransferAmountIsZero() {
        ValidateTransferRequest request = new ValidateTransferRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                OffsetDateTime.now(),
                new Account(1L, Currency.USD, new BigDecimal("1000.00")),
                new Account(2L, Currency.EUR, new BigDecimal("500.00")),
                BigDecimal.ZERO
        );

        BigDecimal exchangeRate = new BigDecimal("0.85");

        when(registryPort.checkIfRequestExist(any(TransferRequestQuery.class))).thenReturn(false);
        when(registryPort.getExchangeRate(request.originator().currency(), request.beneficiary().currency())).thenReturn(Optional.of(exchangeRate));

        assertThatThrownBy(() -> validateTransfer.execute(request))
                .isInstanceOf(RegistryDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", RegistryDomainErrorCode.NEGATIVE_AMOUNT);
    }
}