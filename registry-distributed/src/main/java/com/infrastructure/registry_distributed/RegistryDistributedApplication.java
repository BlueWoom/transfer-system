package com.infrastructure.registry_distributed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableCaching
@EnableFeignClients
@EnableDiscoveryClient
@EnableJpaRepositories
@SpringBootApplication
@EnableTransactionManagement
public class RegistryDistributedApplication {

    public static void main(String[] args) {
        SpringApplication.run(RegistryDistributedApplication.class, args);
    }
}
