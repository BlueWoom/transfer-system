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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProcessTransferTest {

    private RegistryPort registryPort;

    private ProcessTransfer processTransfer;

    @BeforeEach
    void setUp() {
        registryPort = Mockito.mock(RegistryPort.class);
        processTransfer = new ProcessTransfer(registryPort) { };
    }

    @Test
    void shouldProcessTransferSuccessfully() {
        ProcessTransferRequest request = new ProcessTransferRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                OffsetDateTime.now(),
                1L,
                2L,
                BigDecimal.valueOf(100)
        );

        Account originator = new Account(request.originatorId(), Currency.USD, new BigDecimal("1000.00"));
        Account beneficiary = new Account(request.beneficiaryId(), Currency.EUR, new BigDecimal("500.00"));
        BigDecimal exchangeRate = new BigDecimal("0.85");

        when(registryPort.checkIfRequestExist(any(TransferRequestQuery.class))).thenReturn(false);
        when(registryPort.getAccountByIdForUpdate(new AccountQuery(request.originatorId()))).thenReturn(Optional.of(originator));
        when(registryPort.getAccountByIdForUpdate(new AccountQuery(request.beneficiaryId()))).thenReturn(Optional.of(beneficiary));
        when(registryPort.getExchangeRate(Currency.USD, Currency.EUR)).thenReturn(Optional.of(exchangeRate));

        SuccessfulTransfer result = processTransfer.execute(request);

        assertThat(result).isNotNull();
        assertThat(result.getTransferId()).isEqualTo(request.transferId());
        assertThat(result.getRequestId()).isEqualTo(request.requestId());
        assertThat(result.getCredit()).isEqualByComparingTo(request.amount());
        assertThat(result.getDebit()).isEqualByComparingTo(request.amount().multiply(exchangeRate));

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(registryPort, times(2)).updateAccount(accountCaptor.capture());
        Account updatedOriginator = accountCaptor.getAllValues().stream().filter(a -> a.ownerId().equals(originator.ownerId())).findFirst().orElseThrow();
        Account updatedBeneficiary = accountCaptor.getAllValues().stream().filter(a -> a.ownerId().equals(beneficiary.ownerId())).findFirst().orElseThrow();

        assertThat(updatedOriginator.balance()).isEqualByComparingTo(originator.balance().subtract(result.getDebit()));
        assertThat(updatedBeneficiary.balance()).isEqualByComparingTo(beneficiary.balance().add(result.getCredit()));

        ArgumentCaptor<SuccessfulTransfer> successfulTransferCaptor = ArgumentCaptor.forClass(SuccessfulTransfer.class);
        verify(registryPort).createSuccessfulTransfer(successfulTransferCaptor.capture());
        SuccessfulTransfer capturedTransfer = successfulTransferCaptor.getValue();

        assertThat(capturedTransfer.getRequestId()).isEqualTo(request.requestId());
        assertThat(updatedOriginator.balance()).isEqualByComparingTo(capturedTransfer.getOriginator().balance());
        assertThat(updatedBeneficiary.balance()).isEqualByComparingTo(capturedTransfer.getBeneficiary().balance());
    }

    @Test
    void shouldThrowExceptionForDuplicatedRequest() {
        ProcessTransferRequest request = new ProcessTransferRequest(UUID.randomUUID(), UUID.randomUUID(), OffsetDateTime.now(), 1L, 2L, BigDecimal.TEN);
        when(registryPort.checkIfRequestExist(new TransferRequestQuery(request.requestId()))).thenReturn(true);

        assertThatThrownBy(() -> processTransfer.execute(request))
                .isInstanceOf(RegistryDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", RegistryDomainErrorCode.DUPLICATED_REQUEST);
    }

    @Test
    void shouldThrowExceptionWhenOriginatorNotFound() {
        ProcessTransferRequest request = new ProcessTransferRequest(UUID.randomUUID(), UUID.randomUUID(), OffsetDateTime.now(), 1L, 2L, BigDecimal.TEN);
        when(registryPort.checkIfRequestExist(any(TransferRequestQuery.class))).thenReturn(false);
        when(registryPort.getAccountByIdForUpdate(new AccountQuery(request.originatorId()))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> processTransfer.execute(request))
                .isInstanceOf(RegistryDomainException.class)
                .hasMessage("Originator account not found")
                .hasFieldOrPropertyWithValue("errorCode", RegistryDomainErrorCode.ACCOUNT_NOT_FOUND);
    }

    @Test
    void shouldThrowExceptionWhenBeneficiaryNotFound() {
        ProcessTransferRequest request = new ProcessTransferRequest(UUID.randomUUID(), UUID.randomUUID(), OffsetDateTime.now(), 1L, 2L, BigDecimal.TEN);
        Account originator = new Account(request.originatorId(), Currency.USD, new BigDecimal("1000.00"));

        when(registryPort.checkIfRequestExist(any(TransferRequestQuery.class))).thenReturn(false);
        when(registryPort.getAccountByIdForUpdate(new AccountQuery(request.originatorId()))).thenReturn(Optional.of(originator));
        when(registryPort.getAccountByIdForUpdate(new AccountQuery(request.beneficiaryId()))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> processTransfer.execute(request))
                .isInstanceOf(RegistryDomainException.class)
                .hasMessage("Beneficiary account not found")
                .hasFieldOrPropertyWithValue("errorCode", RegistryDomainErrorCode.ACCOUNT_NOT_FOUND);
    }

    @Test
    void shouldThrowExceptionWhenExchangeRateNotFound() {
        ProcessTransferRequest request = new ProcessTransferRequest(UUID.randomUUID(), UUID.randomUUID(), OffsetDateTime.now(), 1L, 2L, BigDecimal.TEN);
        Account originator = new Account(request.originatorId(), Currency.USD, new BigDecimal("1000.00"));
        Account beneficiary = new Account(request.beneficiaryId(), Currency.EUR, new BigDecimal("500.00"));

        when(registryPort.checkIfRequestExist(any(TransferRequestQuery.class))).thenReturn(false);
        when(registryPort.getAccountByIdForUpdate(new AccountQuery(request.originatorId()))).thenReturn(Optional.of(originator));
        when(registryPort.getAccountByIdForUpdate(new AccountQuery(request.beneficiaryId()))).thenReturn(Optional.of(beneficiary));
        when(registryPort.getExchangeRate(originator.currency(), beneficiary.currency())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> processTransfer.execute(request))
                .isInstanceOf(RegistryDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", RegistryDomainErrorCode.EXCHANGE_RATE_NOT_FOUND);
    }

    @Test
    void shouldThrowExceptionWhenExchangeRateIsNegative() {
        ProcessTransferRequest request = new ProcessTransferRequest(UUID.randomUUID(), UUID.randomUUID(), OffsetDateTime.now(), 1L, 2L, BigDecimal.TEN);
        Account originator = new Account(request.originatorId(), Currency.USD, new BigDecimal("1000.00"));
        Account beneficiary = new Account(request.beneficiaryId(), Currency.EUR, new BigDecimal("500.00"));

        when(registryPort.checkIfRequestExist(any(TransferRequestQuery.class))).thenReturn(false);
        when(registryPort.getAccountByIdForUpdate(new AccountQuery(request.originatorId()))).thenReturn(Optional.of(originator));
        when(registryPort.getAccountByIdForUpdate(new AccountQuery(request.beneficiaryId()))).thenReturn(Optional.of(beneficiary));
        when(registryPort.getExchangeRate(originator.currency(), beneficiary.currency())).thenReturn(Optional.of(new BigDecimal("-1")));

        assertThatThrownBy(() -> processTransfer.execute(request))
                .isInstanceOf(RegistryDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", RegistryDomainErrorCode.EXCHANGE_RATE_NEGATIVE);
    }

    @Test
    void shouldThrowExceptionForInsufficientBalance() {
        ProcessTransferRequest request = new ProcessTransferRequest(UUID.randomUUID(), UUID.randomUUID(), OffsetDateTime.now(), 1L, 2L, BigDecimal.valueOf(200));
        Account originator = new Account(request.originatorId(), Currency.USD, new BigDecimal("100.00"));
        Account beneficiary = new Account(request.beneficiaryId(), Currency.EUR, new BigDecimal("500.00"));
        BigDecimal exchangeRate = new BigDecimal("0.85");

        when(registryPort.checkIfRequestExist(any(TransferRequestQuery.class))).thenReturn(false);
        when(registryPort.getAccountByIdForUpdate(new AccountQuery(request.originatorId()))).thenReturn(Optional.of(originator));
        when(registryPort.getAccountByIdForUpdate(new AccountQuery(request.beneficiaryId()))).thenReturn(Optional.of(beneficiary));
        when(registryPort.getExchangeRate(originator.currency(), beneficiary.currency())).thenReturn(Optional.of(exchangeRate));

        assertThatThrownBy(() -> processTransfer.execute(request))
                .isInstanceOf(RegistryDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", RegistryDomainErrorCode.INSUFFICIENT_BALANCE);
    }

    @Test
    void shouldThrowExceptionWhenOriginatorAndBeneficiaryAreEquals() {
        Long ownerId = 1L;
        ProcessTransferRequest request = new ProcessTransferRequest(UUID.randomUUID(), UUID.randomUUID(), OffsetDateTime.now(), ownerId, ownerId, new BigDecimal("100.00"));
        Account originator = new Account(request.originatorId(), Currency.EUR, new BigDecimal("100.00"));
        Account beneficiary = new Account(request.beneficiaryId(), Currency.EUR, new BigDecimal("100.00"));
        BigDecimal exchangeRate = new BigDecimal("1.00");

        when(registryPort.checkIfRequestExist(any(TransferRequestQuery.class))).thenReturn(false);
        when(registryPort.getAccountByIdForUpdate(new AccountQuery(request.originatorId()))).thenReturn(Optional.of(originator));
        when(registryPort.getAccountByIdForUpdate(new AccountQuery(request.beneficiaryId()))).thenReturn(Optional.of(beneficiary));
        when(registryPort.getExchangeRate(originator.currency(), beneficiary.currency())).thenReturn(Optional.of(exchangeRate));

        assertThatThrownBy(() -> processTransfer.execute(request))
                .isInstanceOf(RegistryDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", RegistryDomainErrorCode.INVALID_BENEFICIARY);
    }

    @Test
    void shouldThrowExceptionWhenTransferAmountIsNegative() {
        ProcessTransferRequest request = new ProcessTransferRequest(UUID.randomUUID(), UUID.randomUUID(), OffsetDateTime.now(), 1L, 2L, new BigDecimal("-10"));
        Account originator = new Account(request.originatorId(), Currency.USD, new BigDecimal("100.00"));
        Account beneficiary = new Account(request.beneficiaryId(), Currency.EUR, new BigDecimal("100.00"));
        BigDecimal exchangeRate = new BigDecimal("0.85");

        when(registryPort.checkIfRequestExist(any(TransferRequestQuery.class))).thenReturn(false);
        when(registryPort.getAccountByIdForUpdate(new AccountQuery(request.originatorId()))).thenReturn(Optional.of(originator));
        when(registryPort.getAccountByIdForUpdate(new AccountQuery(request.beneficiaryId()))).thenReturn(Optional.of(beneficiary));
        when(registryPort.getExchangeRate(originator.currency(), beneficiary.currency())).thenReturn(Optional.of(exchangeRate));

        assertThatThrownBy(() -> processTransfer.execute(request))
                .isInstanceOf(RegistryDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", RegistryDomainErrorCode.NEGATIVE_AMOUNT);
    }

    @Test
    void shouldThrowExceptionWhenTransferAmountIsZero() {
        ProcessTransferRequest request = new ProcessTransferRequest(UUID.randomUUID(), UUID.randomUUID(), OffsetDateTime.now(), 1L, 2L, BigDecimal.ZERO);
        Account originator = new Account(request.originatorId(), Currency.USD, new BigDecimal("100.00"));
        Account beneficiary = new Account(request.beneficiaryId(), Currency.EUR, new BigDecimal("100.00"));
        BigDecimal exchangeRate = new BigDecimal("0.85");

        when(registryPort.checkIfRequestExist(any(TransferRequestQuery.class))).thenReturn(false);
        when(registryPort.getAccountByIdForUpdate(new AccountQuery(request.originatorId()))).thenReturn(Optional.of(originator));
        when(registryPort.getAccountByIdForUpdate(new AccountQuery(request.beneficiaryId()))).thenReturn(Optional.of(beneficiary));
        when(registryPort.getExchangeRate(originator.currency(), beneficiary.currency())).thenReturn(Optional.of(exchangeRate));

        assertThatThrownBy(() -> processTransfer.execute(request))
                .isInstanceOf(RegistryDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", RegistryDomainErrorCode.NEGATIVE_AMOUNT);
    }
}