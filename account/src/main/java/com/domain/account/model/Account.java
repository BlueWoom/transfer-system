package com.domain.account.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public class Account {

    private final Long ownerId;

    private final Currency currency;

    private final BigDecimal balance;
}
