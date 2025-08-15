package com.infrastructure.monolith.api.dto;

import com.domain.account.model.Currency;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class AccountDTO {

    private final Long ownerId;

    private final Currency currency;

    private final BigDecimal balance;
}
