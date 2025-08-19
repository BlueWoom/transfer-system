package com.infrastructure.account_distributed.configuration;

import com.infrastructure.account_distributed.database.entity.AccountEntity;
import com.infrastructure.account_distributed.database.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.*;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(AccountRepository accountRepository) {
        return args -> {

            if (accountRepository.count() != 0) {
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

            accountRepository.saveAll(accountEntities);
        };
    }
}