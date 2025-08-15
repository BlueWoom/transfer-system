package com.domain.transfer.model;

import com.domain.transfer.exception.TransferDomainErrorCode;
import com.domain.transfer.exception.TransferDomainException;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString
public class Account {

	private final Long ownerId;
	
	private final Currency currency;
	
	private final BigDecimal balance;

    public Account(Long ownerId, Currency currency, BigDecimal balance) {
        this.ownerId = ownerId;
        this.currency = currency;
        this.balance = balance;
    }

    public Account(Long ownerId, Currency currency) {
        this.ownerId = ownerId;
        this.currency = currency;
        this.balance = BigDecimal.ZERO;
    }

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
