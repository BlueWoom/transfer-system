package com.infrastructure.monolith.configuration;

import com.infrastructure.monolith.external.client.ExchangeRateClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ExchangeRateTestConfig {

    @Bean
    public ExchangeRateClient exchangeRateClient() {
        return new MockExchangeRateClient();
    }
}
