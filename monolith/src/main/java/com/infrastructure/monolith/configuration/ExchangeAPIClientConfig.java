package com.infrastructure.monolith.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import feign.Retryer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.concurrent.TimeUnit;

@Configuration
@Profile("!test")
public class ExchangeAPIClientConfig {

    @Value("${exchange-api.cache.name}")
    private String name;

    @Value("${exchange-api.cache.size}")
    private Integer size;

    @Value("${exchange-api.cache.expiration-hours}")
    private Integer hours;

    @Value("${exchange-api.retryer.period-seconds}")
    private Integer period;

    @Value("${exchange-api.retryer.duration-seconds}")
    private Integer duration;

    @Value("${exchange-api.retryer.max-attempts}")
    private Integer maxAttempts;

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        cacheManager.registerCustomCache(name,
                Caffeine.newBuilder()
                        .maximumSize(size)
                        .expireAfterWrite(hours, TimeUnit.HOURS)
                        .build());

        return cacheManager;
    }

    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(period, TimeUnit.SECONDS.toMillis(duration), maxAttempts);
    }
}