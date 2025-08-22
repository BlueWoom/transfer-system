package com.infrastructure.registry_distributed.usecase.registry.adapter;

import com.domain.registry.model.Currency;
import com.domain.registry.port.RegistryPort;
import com.infrastructure.registry_distributed.external.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegistryAdapter implements RegistryPort {

    private final ExchangeRateService exchangeRateService;

    // Exchange rate operations

    @Override
    @Cacheable(value = "${exchange-api.cache.name}", key = "#source.value + '-' + #destination.value")
    public Optional<BigDecimal> getExchangeRate(Currency source, Currency destination) {
        return exchangeRateService.getExchangeRate(source.getValue(), destination.getValue());
    }
}

