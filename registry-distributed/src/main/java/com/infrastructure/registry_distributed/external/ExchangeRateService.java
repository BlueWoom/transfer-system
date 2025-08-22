package com.infrastructure.registry_distributed.external;

import com.infrastructure.registry_distributed.external.client.ExchangeRateClient;
import com.infrastructure.registry_distributed.external.dto.ExchangeRateResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final ExchangeRateClient exchangeRateClient;

    @Cacheable("${exchange-api.cache.name}")
    public Optional<BigDecimal> getExchangeRate(String source, String destination) {
        if (source.equals(destination)) {
            return Optional.of(BigDecimal.ONE);
        }

        try {
            return Optional.ofNullable(exchangeRateClient.getLatestRates(destination))
                    .map(ExchangeRateResponse::rates)
                    .map(rates -> rates.get(source));

        } catch (FeignException e) {
            return Optional.empty();
        }
    }
}