package com.infrastructure.monolith.external.client;

import com.infrastructure.monolith.configuration.ExchangeAPIClientConfig;
import com.infrastructure.monolith.external.dto.ExchangeRateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Profile("!test")
@FeignClient(name = "exchange-api", url = "${exchange-api.base-url}", configuration = ExchangeAPIClientConfig.class)
public interface ExchangeRateClient {

    @GetMapping("/latest")
    ExchangeRateResponse getLatestRates(@RequestParam("from") String baseCurrency);
}
