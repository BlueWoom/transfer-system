package com.domain.registry.usecase;

import com.domain.registry.exception.RegistryDomainErrorCode;
import com.domain.registry.exception.RegistryDomainException;
import com.domain.registry.model.Account;
import com.domain.registry.model.Currency;
import com.domain.registry.model.SuccessfulTransfer;
import com.domain.registry.port.RegistryPort;
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
import static org.mockito.Mockito.when;

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
        Account originator = Account.builder()
                .ownerId(1L)
                .currency(Currency.USD)
                .balance(new BigDecimal("1000.00"))
                .build();

        Account beneficiary = Account.builder()
                .ownerId(2L)
                .currency(Currency.EUR)
                .balance(new BigDecimal("500.00"))
                .build();

        ValidateTransferRequest request = ValidateTransferRequest.builder()
                .transferId(UUID.randomUUID())
                .createdAt(OffsetDateTime.now())
                .originator(originator)
                .beneficiary(beneficiary)
                .amount(new BigDecimal("100"))
                .build();

        BigDecimal exchangeRate = new BigDecimal("0.85");

        when(registryPort.getExchangeRate(Currency.USD, Currency.EUR)).thenReturn(Optional.of(exchangeRate));

        SuccessfulTransfer result = validateTransfer.execute(request);

        assertThat(result).isNotNull();
        assertThat(result.getTransferId()).isEqualTo(request.transferId());
        assertThat(result.getCredit()).isEqualByComparingTo(request.amount());
        assertThat(result.getDebit()).isEqualByComparingTo(request.amount().multiply(exchangeRate));
    }

    @Test
    void shouldThrowExceptionWhenExchangeRateNotFound() {
        Account originator = Account.builder()
                .ownerId(1L)
                .currency(Currency.USD)
                .balance(new BigDecimal("1000.00"))
                .build();

        Account beneficiary = Account.builder()
                .ownerId(2L)
                .currency(Currency.EUR)
                .balance(new BigDecimal("500.00"))
                .build();

        ValidateTransferRequest request = ValidateTransferRequest.builder()
                .transferId(UUID.randomUUID())
                .createdAt(OffsetDateTime.now())
                .originator(originator)
                .beneficiary(beneficiary)
                .amount(new BigDecimal("100"))
                .build();

        assertThatThrownBy(() -> validateTransfer.execute(request))
                .isInstanceOf(RegistryDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", RegistryDomainErrorCode.EXCHANGE_RATE_NOT_FOUND);
    }

    @Test
    void shouldThrowExceptionWhenExchangeRateIsNegative() {
        Account originator = Account.builder()
                .ownerId(1L)
                .currency(Currency.USD)
                .balance(new BigDecimal("1000.00"))
                .build();

        Account beneficiary = Account.builder()
                .ownerId(2L)
                .currency(Currency.EUR)
                .balance(new BigDecimal("500.00"))
                .build();

        ValidateTransferRequest request = ValidateTransferRequest.builder()
                .transferId(UUID.randomUUID())
                .createdAt(OffsetDateTime.now())
                .originator(originator)
                .beneficiary(beneficiary)
                .amount(new BigDecimal("100"))
                .build();

        when(registryPort.getExchangeRate(request.originator().currency(), request.beneficiary().currency())).thenReturn(Optional.of(new BigDecimal("-1")));

        assertThatThrownBy(() -> validateTransfer.execute(request))
                .isInstanceOf(RegistryDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", RegistryDomainErrorCode.EXCHANGE_RATE_NEGATIVE);
    }

    @Test
    void shouldThrowExceptionForInsufficientBalance() {
        Account originator = Account.builder()
                .ownerId(1L)
                .currency(Currency.USD)
                .balance(new BigDecimal("100.00"))
                .build();

        Account beneficiary = Account.builder()
                .ownerId(2L)
                .currency(Currency.EUR)
                .balance(new BigDecimal("500.00"))
                .build();

        ValidateTransferRequest request = ValidateTransferRequest.builder()
                .transferId(UUID.randomUUID())
                .createdAt(OffsetDateTime.now())
                .originator(originator)
                .beneficiary(beneficiary)
                .amount(new BigDecimal("100"))
                .build();

        BigDecimal exchangeRate = new BigDecimal("1.17");

        when(registryPort.getExchangeRate(request.originator().currency(), request.beneficiary().currency())).thenReturn(Optional.of(exchangeRate));

        assertThatThrownBy(() -> validateTransfer.execute(request))
                .isInstanceOf(RegistryDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", RegistryDomainErrorCode.INSUFFICIENT_BALANCE);
    }

    @Test
    void shouldThrowExceptionWhenOriginatorAndBeneficiaryAreEquals() {
        Account originator = Account.builder()
                .ownerId(1L)
                .currency(Currency.USD)
                .balance(new BigDecimal("1000.00"))
                .build();

        Account beneficiary = Account.builder()
                .ownerId(1L)
                .currency(Currency.EUR)
                .balance(new BigDecimal("500.00"))
                .build();

        ValidateTransferRequest request = ValidateTransferRequest.builder()
                .transferId(UUID.randomUUID())
                .createdAt(OffsetDateTime.now())
                .originator(originator)
                .beneficiary(beneficiary)
                .amount(new BigDecimal("100"))
                .build();

        BigDecimal exchangeRate = new BigDecimal("1.00");

        when(registryPort.getExchangeRate(request.originator().currency(), request.beneficiary().currency())).thenReturn(Optional.of(exchangeRate));

        assertThatThrownBy(() -> validateTransfer.execute(request))
                .isInstanceOf(RegistryDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", RegistryDomainErrorCode.INVALID_BENEFICIARY);
    }

    @Test
    void shouldThrowExceptionWhenTransferAmountIsNegative() {
        Account originator = Account.builder()
                .ownerId(1L)
                .currency(Currency.USD)
                .balance(new BigDecimal("1000.00"))
                .build();

        Account beneficiary = Account.builder()
                .ownerId(2L)
                .currency(Currency.EUR)
                .balance(new BigDecimal("500.00"))
                .build();

        ValidateTransferRequest request = ValidateTransferRequest.builder()
                .transferId(UUID.randomUUID())
                .createdAt(OffsetDateTime.now())
                .originator(originator)
                .beneficiary(beneficiary)
                .amount(new BigDecimal("-100"))
                .build();

        BigDecimal exchangeRate = new BigDecimal("0.85");

        when(registryPort.getExchangeRate(request.originator().currency(), request.beneficiary().currency())).thenReturn(Optional.of(exchangeRate));

        assertThatThrownBy(() -> validateTransfer.execute(request))
                .isInstanceOf(RegistryDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", RegistryDomainErrorCode.NEGATIVE_AMOUNT);
    }

    @Test
    void shouldThrowExceptionWhenTransferAmountIsZero() {
        Account originator = Account.builder()
                .ownerId(1L)
                .currency(Currency.USD)
                .balance(new BigDecimal("1000.00"))
                .build();

        Account beneficiary = Account.builder()
                .ownerId(2L)
                .currency(Currency.EUR)
                .balance(new BigDecimal("500.00"))
                .build();

        ValidateTransferRequest request = ValidateTransferRequest.builder()
                .transferId(UUID.randomUUID())
                .createdAt(OffsetDateTime.now())
                .originator(originator)
                .beneficiary(beneficiary)
                .amount(BigDecimal.ZERO)
                .build();

        BigDecimal exchangeRate = new BigDecimal("0.85");

        when(registryPort.getExchangeRate(request.originator().currency(), request.beneficiary().currency())).thenReturn(Optional.of(exchangeRate));

        assertThatThrownBy(() -> validateTransfer.execute(request))
                .isInstanceOf(RegistryDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", RegistryDomainErrorCode.NEGATIVE_AMOUNT);
    }
}