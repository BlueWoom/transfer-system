package com.domain.transfer.usecase;

import com.domain.transfer.exception.TransferDomainErrorCode;
import com.domain.transfer.exception.TransferDomainException;
import com.domain.transfer.model.*;
import com.domain.transfer.port.TransferPort;
import com.domain.transfer.port.query.AccountQuery;
import com.domain.transfer.port.query.TransferQuery;
import com.domain.transfer.usecase.request.TransferRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AcceptTransferTest {

    private TransferPort transferPort;

    private AcceptTransfer usecase;

    @BeforeEach
    void setUp() {
        transferPort = mock(TransferPort.class);
        usecase = new AcceptTransfer(transferPort) {
        };
    }

    @Test
    void shouldCreatePendingTransferSuccessfully() {
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        Currency usd = Currency.USD;
        Currency eur = Currency.EUR;
        BigDecimal amount = new BigDecimal("100.0");
        BigDecimal exchangeRate = new BigDecimal("2.0");
        Account originator = new Account(1L, usd, new BigDecimal("200.0"));
        Account beneficiary = new Account(2L, eur, new BigDecimal("0.0"));

        when(transferPort.checkIfRequestExist(new TransferQuery(requestId))).thenReturn(false);
        when(transferPort.getAccount(new AccountQuery(originator.ownerId()))).thenReturn(Optional.of(originator));
        when(transferPort.getAccount(new AccountQuery(beneficiary.ownerId()))).thenReturn(Optional.of(beneficiary));
        when(transferPort.getExchangeRate(usd, eur)).thenReturn(Optional.of(exchangeRate));

        TransferRequest request = new TransferRequest(requestId, originator.ownerId(), beneficiary.ownerId(), amount);

        PendingTransfer actualTransfer = usecase.execute(request);

        PendingTransfer expectedTransfer = new PendingTransfer(transferId, requestId, OffsetDateTime.now(), amount, originator, beneficiary);

        assertThat(actualTransfer)
                .usingRecursiveComparison()
                .ignoringFields("transferId", "createdAt")
                .isEqualTo(expectedTransfer);

        ArgumentCaptor<PendingTransfer> captor = ArgumentCaptor.forClass(PendingTransfer.class);
        verify(transferPort).createPendingTransfer(captor.capture());
        assertThat(captor.getValue())
                .usingRecursiveComparison()
                .ignoringFields("transferId", "createdAt")
                .isEqualTo(expectedTransfer);
    }

    @Test
    void shouldThrowOnDuplicatedRequest() {
        UUID requestId = UUID.randomUUID();
        when(transferPort.checkIfRequestExist(new TransferQuery(requestId))).thenReturn(true);

        TransferRequest request = new TransferRequest(requestId, 1L, 2L, new BigDecimal("100.0"));

        assertThatThrownBy(() -> usecase.execute(request))
                .isInstanceOf(TransferDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", TransferDomainErrorCode.DUPLICATED_REQUEST);
    }

    @Test
    void shouldThrowWhenOriginatorAccountNotFound() {
        UUID requestId = UUID.randomUUID();
        when(transferPort.checkIfRequestExist(new TransferQuery(requestId))).thenReturn(false);
        when(transferPort.getAccount(any(AccountQuery.class))).thenReturn(Optional.empty());

        TransferRequest request = new TransferRequest(requestId, 1L, 2L, new BigDecimal("100.0"));

        assertThatThrownBy(() -> usecase.execute(request))
                .isInstanceOf(TransferDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", TransferDomainErrorCode.ACCOUNT_NOT_FOUND);
    }

    @Test
    void shouldThrowWhenBeneficiaryAccountNotFound() {
        UUID requestId = UUID.randomUUID();
        Long originatorId = 1L;
        Long beneficiaryId = 2L;
        Account originator = new Account(originatorId, Currency.USD, new BigDecimal("100.0"));

        when(transferPort.checkIfRequestExist(new TransferQuery(requestId))).thenReturn(false);
        when(transferPort.getAccount(new AccountQuery(originatorId))).thenReturn(Optional.of(originator));
        when(transferPort.getAccount(new AccountQuery(beneficiaryId))).thenReturn(Optional.empty());

        TransferRequest request = new TransferRequest(requestId, originatorId, beneficiaryId, new BigDecimal("100.0"));

        assertThatThrownBy(() -> usecase.execute(request))
                .isInstanceOf(TransferDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", TransferDomainErrorCode.ACCOUNT_NOT_FOUND);
    }

    @Test
    void shouldThrowWhenExchangeRateNotFound() {
        UUID requestId = UUID.randomUUID();
        Long originatorId = 1L;
        Long beneficiaryId = 2L;
        Account originator = new Account(originatorId, Currency.USD, new BigDecimal("1000.0"));
        Account beneficiary = new Account(beneficiaryId, Currency.EUR, new BigDecimal("0.0"));

        when(transferPort.checkIfRequestExist(new TransferQuery(requestId))).thenReturn(false);
        when(transferPort.getAccount(new AccountQuery(originatorId))).thenReturn(Optional.of(originator));
        when(transferPort.getAccount(new AccountQuery(beneficiaryId))).thenReturn(Optional.of(beneficiary));
        when(transferPort.getExchangeRate(Currency.USD, Currency.EUR)).thenReturn(Optional.empty());

        TransferRequest request = new TransferRequest(requestId, originatorId, beneficiaryId, new BigDecimal("100.0"));

        assertThatThrownBy(() -> usecase.execute(request))
                .isInstanceOf(TransferDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", TransferDomainErrorCode.EXCHANGE_RATE_NOT_FOUND);
    }

    @Test
    void shouldThrowWhenInsufficientBalance() {
        UUID requestId = UUID.randomUUID();
        Long originatorId = 1L;
        Long beneficiaryId = 2L;
        BigDecimal originatorRate = new BigDecimal("2.0");
        Account originator = new Account(originatorId, Currency.USD, new BigDecimal("100.0"));
        Account beneficiary = new Account(beneficiaryId, Currency.EUR, new BigDecimal("0.0"));

        when(transferPort.checkIfRequestExist(new TransferQuery(requestId))).thenReturn(false);
        when(transferPort.getAccount(new AccountQuery(originatorId))).thenReturn(Optional.of(originator));
        when(transferPort.getAccount(new AccountQuery(beneficiaryId))).thenReturn(Optional.of(beneficiary));
        when(transferPort.getExchangeRate(Currency.USD, Currency.EUR)).thenReturn(Optional.of(originatorRate));

        TransferRequest request = new TransferRequest(requestId, originatorId, beneficiaryId, new BigDecimal("100.0"));

        assertThatThrownBy(() -> usecase.execute(request))
                .isInstanceOf(TransferDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", TransferDomainErrorCode.INSUFFICIENT_BALANCE);
    }
}