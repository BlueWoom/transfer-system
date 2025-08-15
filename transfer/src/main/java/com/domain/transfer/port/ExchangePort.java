package com.domain.transfer.port;

import com.domain.transfer.model.Currency;

import java.math.BigDecimal;
import java.util.Optional;

public interface ExchangePort {

    Optional<BigDecimal> getExchangeRate(Currency source, Currency destination);
}
