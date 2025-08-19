package com.domain.registry.model;

import com.domain.registry.exception.RegistryDomainErrorCode;
import com.domain.registry.exception.RegistryDomainException;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
public final class SuccessfulTransfer extends Transfer {

    private final BigDecimal transferAmount;

    private final Account originator;

    private final Account beneficiary;

    private final OffsetDateTime processedAt;

    private final BigDecimal exchangeRate;

    private final BigDecimal debit;

    private final BigDecimal credit;

    public SuccessfulTransfer(UUID transferId, UUID requestId, OffsetDateTime createdAt, BigDecimal transferAmount, Account originator, Account beneficiary, OffsetDateTime processedAt, BigDecimal exchangeRate, BigDecimal debit, BigDecimal credit) {
        super(transferId, requestId, createdAt);

        verifyNotIncomplete(transferAmount, originator, beneficiary, processedAt, exchangeRate, debit, credit);
        verifyNotSameOwnerOrBeneficiary(originator, beneficiary);
        varifyNotZeroOrNegativeAmount(transferAmount);
        verifyNotZeroOrNegativeDebit(debit);
        verifyNotZeroOrNegativeCredit(credit);

        this.transferAmount = transferAmount;
        this.originator = originator;
        this.beneficiary = beneficiary;
        this.processedAt = processedAt;
        this.exchangeRate = exchangeRate;
        this.debit = debit;
        this.credit = credit;
    }

    private void varifyNotZeroOrNegativeAmount(BigDecimal transferAmount) {
        if (transferAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegistryDomainException(RegistryDomainErrorCode.NEGATIVE_AMOUNT, "Transfer amount must be greater than zero.");
        }
    }

    private void verifyNotZeroOrNegativeDebit(BigDecimal debit) {
        if (debit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegistryDomainException(RegistryDomainErrorCode.NEGATIVE_AMOUNT, "Debit amount must be greater than zero.");
        }
    }

    private void verifyNotZeroOrNegativeCredit(BigDecimal credit) {
        if (credit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegistryDomainException(RegistryDomainErrorCode.NEGATIVE_AMOUNT, "Debit amount must be greater than zero.");
        }
    }

    private void verifyNotSameOwnerOrBeneficiary(Account originator, Account beneficiary) {
        if (originator.ownerId().equals(beneficiary.ownerId())) {
            throw new RegistryDomainException(RegistryDomainErrorCode.INVALID_BENEFICIARY, "Originator and beneficiary cannot be the same.");
        }
    }

    private void verifyNotIncomplete(BigDecimal transferAmount, Account originator, Account beneficiary, OffsetDateTime processedAt, BigDecimal exchangeRate, BigDecimal debit, BigDecimal credit) {
        if (transferAmount == null ||
            originator == null ||
            beneficiary == null ||
            processedAt == null ||
            exchangeRate == null ||
            debit == null ||
            credit == null) {

            throw new RegistryDomainException(RegistryDomainErrorCode.INVALID_TRANSFER, "Transfer is not processable due to missing fields.");
        }
    }
}