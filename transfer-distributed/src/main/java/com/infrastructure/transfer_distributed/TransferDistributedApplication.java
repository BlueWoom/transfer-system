package com.infrastructure.transfer_distributed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableDiscoveryClient
@EnableJpaRepositories
@SpringBootApplication
@EnableTransactionManagement
public class TransferDistributedApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransferDistributedApplication.class, args);
	}
}
