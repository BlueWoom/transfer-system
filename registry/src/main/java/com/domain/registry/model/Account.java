package com.domain.registry.model;

import com.domain.registry.exception.RegistryDomainErrorCode;
import com.domain.registry.exception.RegistryDomainException;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record Account(Long ownerId, Currency currency, BigDecimal balance) {

    public boolean hasFund(BigDecimal amount) {
        return balance.compareTo(amount) >= 0;
    }

    public Account debit(BigDecimal amount) {
        if (!hasFund(amount)) {
            throw new RegistryDomainException(RegistryDomainErrorCode.INSUFFICIENT_BALANCE, "Insufficient funds for debit operation.");
        }

        return Account.builder()
                .ownerId(ownerId)
                .currency(currency)
                .balance(balance.subtract(amount))
                .build();
    }

    public Account credit(BigDecimal amount) {
        return Account.builder()
                .ownerId(ownerId)
                .currency(currency)
                .balance(balance.add(amount))
                .build();
    }
}
