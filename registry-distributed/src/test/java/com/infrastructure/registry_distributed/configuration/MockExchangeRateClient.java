package com.infrastructure.registry_distributed.configuration;

import com.infrastructure.registry_distributed.external.client.ExchangeRateClient;
import com.infrastructure.registry_distributed.external.dto.ExchangeRateResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class MockExchangeRateClient implements ExchangeRateClient {

    @Override
    public ExchangeRateResponse getLatestRates(String baseCurrency) {
        if (baseCurrency.equals("USD")) {
            return new ExchangeRateResponse(new BigDecimal("1.0"), "USD", LocalDate.now(), Map.of("EUR", new BigDecimal("0.85668")));
        }

        return null;
    }
}
