package com.domain.transfer.usecase;

import com.domain.transfer.exception.TransferDomainErrorCode;
import com.domain.transfer.exception.TransferDomainException;
import com.domain.transfer.model.Account;
import com.domain.transfer.model.Currency;
import com.domain.transfer.model.PendingTransfer;
import com.domain.transfer.model.SuccessTransfer;
import com.domain.transfer.port.AccountPort;
import com.domain.transfer.port.ExchangePort;
import com.domain.transfer.port.TransferPort;
import com.domain.transfer.port.query.AccountQuery;
import com.domain.transfer.port.query.ProcessTransferQuery;
import com.domain.transfer.usecase.request.ProcessTransferRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ProcessTransferTest {

    private AccountPort accountPort;

    private ExchangePort exchangePort;

    private TransferPort transferPort;

    private ProcessTransfer usecase;

    @BeforeEach
    void setUp() {
        accountPort = mock(AccountPort.class);
        transferPort = mock(TransferPort.class);
        exchangePort = mock(ExchangePort.class);
        usecase = new ProcessTransfer(accountPort, exchangePort, transferPort) {};
    }

    @Test
    void shouldProcessTransferSuccessfully() {
        // Arrange
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        Currency usd = Currency.USD;
        Currency eur = Currency.EUR;
        BigDecimal amount = new BigDecimal("100.0");
        BigDecimal exchangeRate = new BigDecimal("1.17");
        Account originator = new Account(1L, usd, new BigDecimal("200.0"));
        Account beneficiary = new Account(2L, eur, new BigDecimal("0.0"));

        PendingTransfer pendingTransfer = new PendingTransfer(transferId, requestId, OffsetDateTime.now(), amount, originator, beneficiary);

        when(transferPort.getPendingTransferForUpdate(new ProcessTransferQuery(transferId))).thenReturn(Optional.of(pendingTransfer));
        when(accountPort.getAccountByIdForUpdate(new AccountQuery(originator.getOwnerId()))).thenReturn(Optional.of(originator));
        when(accountPort.getAccountByIdForUpdate(new AccountQuery(beneficiary.getOwnerId()))).thenReturn(Optional.of(beneficiary));
        when(exchangePort.getExchangeRate(usd, eur)).thenReturn(Optional.of(exchangeRate));

        // Act
        ProcessTransferRequest request = new ProcessTransferRequest(transferId);
        SuccessTransfer result = usecase.execute(request);

        // Assert
        SuccessTransfer expectedTransfer = new SuccessTransfer(pendingTransfer, OffsetDateTime.now(), exchangeRate, amount.multiply(exchangeRate), amount);
        Account expectedOriginatorAfterDebit = new Account(1L, usd, new BigDecimal("83.000"));
        Account expectedBeneficiaryAfterCredit = new Account(2L, eur, new BigDecimal("100.0"));

        // --- Assert the use case return value ---
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "processedAt")
                .isEqualTo(expectedTransfer);

        // --- Verify and assert the saved transfer object ---
        ArgumentCaptor<SuccessTransfer> transferCaptor = ArgumentCaptor.forClass(SuccessTransfer.class);
        verify(transferPort).save(transferCaptor.capture());
        assertThat(transferCaptor.getValue())
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "processedAt")
                .isEqualTo(expectedTransfer);

        // --- Verify and assert the saved account objects ---
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountPort, times(2)).save(accountCaptor.capture());

        List<Account> savedAccounts = accountCaptor.getAllValues();

        // Assert that the two saved accounts match the expected debited and credited accounts
        assertThat(savedAccounts)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(expectedOriginatorAfterDebit, expectedBeneficiaryAfterCredit);
    }

    @Test
    void shouldThrowWhenTransferNotFound() {
        UUID transferId = UUID.randomUUID();
        when(transferPort.getPendingTransferForUpdate(any(ProcessTransferQuery.class))).thenReturn(Optional.empty());

        ProcessTransferRequest request = new ProcessTransferRequest(transferId);

        assertThatThrownBy(() -> usecase.execute(request))
                .isInstanceOf(TransferDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", TransferDomainErrorCode.TRANSFER_NOT_FOUND);
    }

    @Test
    void shouldThrowWhenOriginatorAccountNotFound() {
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        BigDecimal transferAmount = new BigDecimal("100.0");
        Currency eur = Currency.EUR;
        Currency usd = Currency.USD;
        Account originator = new Account(1L, usd, new BigDecimal("200.0"));
        Account beneficiary = new Account(2L, eur, new BigDecimal("0.0"));


        PendingTransfer pendingTransfer = new PendingTransfer(transferId, requestId, OffsetDateTime.now(), transferAmount, originator, beneficiary);

        when(transferPort.getPendingTransferForUpdate(any(ProcessTransferQuery.class))).thenReturn(Optional.of(pendingTransfer));
        when(accountPort.getAccountByIdForUpdate(any(AccountQuery.class))).thenReturn(Optional.empty());

        ProcessTransferRequest request = new ProcessTransferRequest(transferId);

        assertThatThrownBy(() -> usecase.execute(request))
                .isInstanceOf(TransferDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", TransferDomainErrorCode.ACCOUNT_NOT_FOUND);
    }

    @Test
    void shouldThrowWhenBeneficiaryAccountNotFound() {
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        BigDecimal transferAmount = new BigDecimal("100.0");
        Currency eur = Currency.EUR;
        Currency usd = Currency.USD;
        Account originator = new Account(1L, usd, new BigDecimal("200.0"));
        Account beneficiary = new Account(2L, eur, new BigDecimal("0.0"));

        PendingTransfer pendingTransfer = new PendingTransfer(transferId, requestId, OffsetDateTime.now(), transferAmount, originator, beneficiary);

        when(transferPort.getPendingTransferForUpdate(any(ProcessTransferQuery.class))).thenReturn(Optional.of(pendingTransfer));
        when(accountPort.getAccountByIdForUpdate(new AccountQuery(beneficiary.getOwnerId()))).thenReturn(Optional.empty());
        when(accountPort.getAccountByIdForUpdate(new AccountQuery(originator.getOwnerId()))).thenReturn(Optional.of(originator));

        ProcessTransferRequest request = new ProcessTransferRequest(transferId);

        assertThatThrownBy(() -> usecase.execute(request))
                .isInstanceOf(TransferDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", TransferDomainErrorCode.ACCOUNT_NOT_FOUND);
    }

    @Test
    void shouldThrowWhenInsufficientBalance() {
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        BigDecimal transferAmount = new BigDecimal("100.0");
        BigDecimal exchangeRate = new BigDecimal("2.0");
        Currency eur = Currency.EUR;
        Currency usd = Currency.USD;
        Account originator = new Account(1L, usd, new BigDecimal("100.0"));
        Account beneficiary = new Account(2L, eur, new BigDecimal("0.0"));

        PendingTransfer pendingTransfer = new PendingTransfer(transferId, requestId, OffsetDateTime.now(), transferAmount, originator, beneficiary);

        when(transferPort.getPendingTransferForUpdate(any(ProcessTransferQuery.class))).thenReturn(Optional.of(pendingTransfer));
        when(accountPort.getAccountByIdForUpdate(new AccountQuery(originator.getOwnerId()))).thenReturn(Optional.of(originator));
        when(accountPort.getAccountByIdForUpdate(new AccountQuery(beneficiary.getOwnerId()))).thenReturn(Optional.of(beneficiary));
        when(exchangePort.getExchangeRate(usd, eur)).thenReturn(Optional.of(exchangeRate));

        ProcessTransferRequest request = new ProcessTransferRequest(transferId);

        assertThatThrownBy(() -> usecase.execute(request))
                .isInstanceOf(TransferDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", TransferDomainErrorCode.INSUFFICIENT_BALANCE);
    }
}