package com.infrastructure.monolith.usecase.registry.adapter;

import com.domain.registry.model.Currency;
import com.domain.registry.port.RegistryPort;
import com.domain.registry.port.query.TransferRequestQuery;
import com.infrastructure.monolith.database.repository.TransferService;
import com.infrastructure.monolith.external.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegistryAdapter implements RegistryPort {

    private final TransferService transferRepository;

    private final ExchangeRateService exchangeRateService;

    // Transfer operations

    @Override
    public boolean checkIfRequestExist(TransferRequestQuery transferRequestQuery) {
        return transferRepository.existsByRequestId(transferRequestQuery.requestId());
    }

    // Exchange rate operations

    @Override
    @Cacheable(value = "${exchange-api.cache.name}", key = "#source.value + '-' + #destination.value")
    public Optional<BigDecimal> getExchangeRate(Currency source, Currency destination) {
        return exchangeRateService.getExchangeRate(source.getValue(), destination.getValue());
    }
}

