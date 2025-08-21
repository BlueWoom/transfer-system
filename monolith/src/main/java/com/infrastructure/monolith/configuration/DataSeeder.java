package com.infrastructure.monolith.configuration;

import com.domain.registry.model.Currency;
import com.infrastructure.monolith.database.entity.AccountEntity;
import com.infrastructure.monolith.database.repository.AccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Configuration
public class DataSeeder {

    private final Random rand = new Random();

    @Bean
    CommandLineRunner seedData(AccountService accountRepository) {
        return args -> {

            if (accountRepository.count() != 0) {
                return; // Data already exists, skip seeding
            }

            List<AccountEntity> accountEntities = new ArrayList<>();
            List<String> currencies = Arrays.stream(Currency.values()).map(Currency::getValue).toList();

            // Create random accounts
            for (int i = 0; i < 100; i++) {
                AccountEntity account = AccountEntity.builder()
                        .ownerId(1000L + i)
                        .currency(currencies.get(rand.nextInt(currencies.size())))
                        .balance(new BigDecimal("1000"))
                        .build();

                accountEntities.add(account);
            }

            accountRepository.saveAll(accountEntities);
        };
    }
}