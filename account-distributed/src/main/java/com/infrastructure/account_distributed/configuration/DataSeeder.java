package com.infrastructure.account_distributed.configuration;

import com.infrastructure.account_distributed.database.entity.AccountEntity;
import com.infrastructure.account_distributed.database.repository.AccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(AccountService accountService) {
        return args -> {

            if (accountService.count() != 0) {
                return; // Data already exists, skip seeding
            }

            List<AccountEntity> accountEntities = new ArrayList<>();

            // Create random accounts
            for (int i = 0; i < 100; i++) {
                AccountEntity account = AccountEntity.builder()
                        .ownerId(1000L + i)
                        .currency("USD")
                        .balance(new BigDecimal("1000"))
                        .build();

                accountEntities.add(account);
            }

            accountService.saveAll(accountEntities);
        };
    }
}