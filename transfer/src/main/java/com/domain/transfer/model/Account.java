package com.domain.transfer.model;

import com.domain.transfer.exception.TransferDomainErrorCode;
import com.domain.transfer.exception.TransferDomainException;

import java.math.BigDecimal;

public record Account(Long ownerId, Currency currency, BigDecimal balance) {

    public boolean hasFund(BigDecimal amount) {
        return balance.compareTo(amount) >= 0;
    }

    public Account debit(BigDecimal amount) {
        if (!hasFund(amount)) {
            throw new TransferDomainException(TransferDomainErrorCode.INSUFFICIENT_BALANCE, "Insufficient funds for debit operation.");
        }

        return new Account(ownerId, currency, balance.subtract(amount));
    }

    public Account credit(BigDecimal amount) {
        return new Account(ownerId, currency, balance.add(amount));
    }
}
