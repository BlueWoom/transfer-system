package com.domain.registry.port;

import com.domain.registry.model.Currency;
import com.domain.registry.port.query.TransferRequestQuery;

import java.math.BigDecimal;
import java.util.Optional;

public interface RegistryPort {

    // Transfer operations

    boolean checkIfRequestExist(TransferRequestQuery transferRequestQuery);

    // Exchange rate operations

    Optional<BigDecimal> getExchangeRate(Currency source, Currency destination);
}
