package com.domain.transfer.model;

import com.domain.transfer.exception.TransferDomainErrorCode;
import com.domain.transfer.exception.TransferDomainException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class PendingTransferTest {

    @Test
    void shouldCreatePendingTransferSuccessfully() {
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        BigDecimal transferAmount = BigDecimal.valueOf(100.00);
        Account originator = new Account(1L, Currency.USD, BigDecimal.ZERO);
        Account beneficiary = new Account(2L, Currency.EUR, BigDecimal.ZERO);

        PendingTransfer pendingTransfer = new PendingTransfer(transferId, requestId, createdAt, transferAmount, originator, beneficiary);

        assertThat(pendingTransfer).isNotNull();
        assertThat(pendingTransfer.getTransferId()).isEqualTo(transferId);
        assertThat(pendingTransfer.getRequestId()).isEqualTo(requestId);
        assertThat(pendingTransfer.getCreatedAt()).isEqualTo(createdAt);
        assertThat(pendingTransfer.getTransferAmount()).isEqualTo(transferAmount);
        assertThat(pendingTransfer.getOriginator()).isEqualTo(originator);
        assertThat(pendingTransfer.getBeneficiary()).isEqualTo(beneficiary);
    }

    @Test
    void shouldThrowExceptionWhenTransferIdIsNull() {
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        BigDecimal transferAmount = BigDecimal.valueOf(100.00);
        Account originator = new Account(1L, Currency.USD, BigDecimal.ZERO);
        Account beneficiary = new Account(2L, Currency.EUR, BigDecimal.ZERO);

        assertThatThrownBy(() -> new PendingTransfer(null, requestId, createdAt, transferAmount, originator, beneficiary))
                .isInstanceOf(TransferDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", TransferDomainErrorCode.INVALID_TRANSFER)
                .hasMessage("Transfer is invalid due to missing fields.");
    }

    @Test
    void shouldThrowExceptionWhenRequestIdIsNull() {
        UUID transferId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        BigDecimal transferAmount = BigDecimal.valueOf(100.00);
        Account originator = new Account(1L, Currency.USD, BigDecimal.ZERO);
        Account beneficiary = new Account(2L, Currency.EUR, BigDecimal.ZERO);

        assertThatThrownBy(() -> new PendingTransfer(transferId, null, createdAt, transferAmount, originator, beneficiary))
                .isInstanceOf(TransferDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", TransferDomainErrorCode.INVALID_TRANSFER)
                .hasMessage("Transfer is invalid due to missing fields.");
    }

    @Test
    void shouldThrowExceptionWhenCreatedAtIsNull() {
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        BigDecimal transferAmount = BigDecimal.valueOf(100.00);
        Account originator = new Account(1L, Currency.USD, BigDecimal.ZERO);
        Account beneficiary = new Account(2L, Currency.EUR, BigDecimal.ZERO);

        assertThatThrownBy(() -> new PendingTransfer(transferId, requestId, null, transferAmount, originator, beneficiary))
                .isInstanceOf(TransferDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", TransferDomainErrorCode.INVALID_TRANSFER)
                .hasMessage("Transfer is invalid due to missing fields.");
    }

    @Test
    void shouldThrowExceptionWhenTransferAmountIsNull() {
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        Account originator = new Account(1L, Currency.USD, BigDecimal.ZERO);
        Account beneficiary = new Account(2L, Currency.EUR, BigDecimal.ZERO);

        assertThatThrownBy(() -> new PendingTransfer(transferId, requestId, createdAt, null, originator, beneficiary))
                .isInstanceOf(TransferDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", TransferDomainErrorCode.INVALID_TRANSFER)
                .hasMessage("Transfer is not processable due to missing fields.");
    }

    @Test
    void shouldThrowExceptionWhenOriginatorIsNull() {
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        BigDecimal transferAmount = BigDecimal.valueOf(100.00);
        Account beneficiary = new Account(2L, Currency.EUR, BigDecimal.ZERO);

        assertThatThrownBy(() -> new PendingTransfer(transferId, requestId, createdAt, transferAmount, null, beneficiary))
                .isInstanceOf(TransferDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", TransferDomainErrorCode.INVALID_TRANSFER)
                .hasMessage("Transfer is not processable due to missing fields.");
    }

    @Test
    void shouldThrowExceptionWhenBeneficiaryIsNull() {
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        BigDecimal transferAmount = BigDecimal.valueOf(100.00);
        Account originator = new Account(1L, Currency.USD, BigDecimal.ZERO);

        assertThatThrownBy(() -> new PendingTransfer(transferId, requestId, createdAt, transferAmount, originator, null))
                .isInstanceOf(TransferDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", TransferDomainErrorCode.INVALID_TRANSFER)
                .hasMessage("Transfer is not processable due to missing fields.");
    }

    @Test
    void shouldThrowExceptionWhenTransferAmountIsZero() {
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        BigDecimal transferAmount = BigDecimal.ZERO;
        Account originator = new Account(1L, Currency.USD, BigDecimal.ZERO);
        Account beneficiary = new Account(2L, Currency.EUR, BigDecimal.ZERO);

        assertThatThrownBy(() -> new PendingTransfer(transferId, requestId, createdAt, transferAmount, originator, beneficiary))
                .isInstanceOf(TransferDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", TransferDomainErrorCode.NEGATIVE_AMOUNT)
                .hasMessage("Transfer amount must be greater than zero.");
    }

    @Test
    void shouldThrowExceptionWhenTransferAmountIsNegative() {
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        BigDecimal transferAmount = BigDecimal.valueOf(-50.00);
        Account originator = new Account(1L, Currency.USD, BigDecimal.ZERO);
        Account beneficiary = new Account(2L, Currency.EUR, BigDecimal.ZERO);

        assertThatThrownBy(() -> new PendingTransfer(transferId, requestId, createdAt, transferAmount, originator, beneficiary))
                .isInstanceOf(TransferDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", TransferDomainErrorCode.NEGATIVE_AMOUNT)
                .hasMessage("Transfer amount must be greater than zero.");
    }

    @Test
    void shouldThrowExceptionWhenOriginatorAndBeneficiaryAreTheSame() {
        Long sameOwnerId = 1L;
        Account sameAccount = new Account(sameOwnerId, Currency.USD, BigDecimal.ZERO);
        UUID transferId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        BigDecimal transferAmount = BigDecimal.valueOf(100.00);

        assertThatThrownBy(() -> new PendingTransfer(transferId, requestId, createdAt, transferAmount, sameAccount, sameAccount))
                .isInstanceOf(TransferDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", TransferDomainErrorCode.INVALID_BENEFICIARY)
                .hasMessage("Originator and beneficiary cannot be the same.");
    }
}