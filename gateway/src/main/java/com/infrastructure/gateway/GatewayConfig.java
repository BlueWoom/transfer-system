package com.infrastructure.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("account-service-route", r -> r
                        .path("/account/**", "/accounts/**")
                        .uri("lb://ACCOUNT-DISTRIBUTED"))

                .route("transfer-service-route", r -> r
                        .path("/send-request-transfer/**")
                        .uri("lb://TRANSFER-DISTRIBUTED"))

                .build();
    }
}