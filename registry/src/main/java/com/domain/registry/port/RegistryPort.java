package com.domain.registry.port;

import com.domain.registry.model.Currency;

import java.math.BigDecimal;
import java.util.Optional;

public interface RegistryPort {

    // Exchange rate operations

    Optional<BigDecimal> getExchangeRate(Currency source, Currency destination);
}
