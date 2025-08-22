package com.infrastructure.registry_distributed.configuration;

import com.infrastructure.registry_distributed.external.client.ExchangeRateClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ExchangeRateTestConfig {

    @Bean
    public ExchangeRateClient exchangeRateClient() {
        return new MockExchangeRateClient();
    }
}
