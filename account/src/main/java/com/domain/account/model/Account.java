package com.domain.account.model;

import java.math.BigDecimal;

public record Account(Long ownerId, String currency, BigDecimal balance) {

}
