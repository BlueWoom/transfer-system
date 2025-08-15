package com.infrastructure.monolith.configuration;

import com.infrastructure.monolith.database.entity.*;
import com.infrastructure.monolith.database.repository.AccountRepository;
import com.infrastructure.monolith.database.repository.TransferRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

@Configuration
@Profile("dev")
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(AccountRepository accountRepository, TransferRepository transferRepository) {
        return args -> {

            if (accountRepository.count() != 0 || transferRepository.count() != 0) {
                return; // Data already exists, skip seeding
            }

            Random rand = new Random();
            List<AccountEntity> accountEntities = new ArrayList<>();
            List<TransferEntity> transferEntities = new ArrayList<>();
            List<String> currencies = List.of("USD", "EUR");
            List<TransferStatus> statuses = List.of(TransferStatus.FAILED, TransferStatus.SUCCESS, TransferStatus.PENDING);

            // Create random accounts
            for (int i = 0; i < 1000; i++) {
                AccountEntity account = AccountEntity.builder()
                        .ownerId(1000L + i)
                        .currency(currencies.get(rand.nextInt(currencies.size())))
                        .build();

                accountEntities.add(account);
            }

            // Create random transfers
            for (int i = 0; i < 10000; i++) {
                AccountEntity originator = accountEntities.get(rand.nextInt(accountEntities.size()));
                AccountEntity beneficiary;
                do beneficiary = accountEntities.get(rand.nextInt(accountEntities.size())); while (beneficiary.equals(originator));

                OffsetDateTime createdAt = OffsetDateTime.now().minusDays(rand.nextInt(365)).minusHours(rand.nextInt(24)).minusMinutes(rand.nextInt(60));
                OffsetDateTime processedAt = createdAt.plusSeconds(rand.nextInt((int) (OffsetDateTime.now().toEpochSecond() - createdAt.toEpochSecond())));

                TransferEntity transfer = TransferEntity.builder()
                        .transferId(UUID.randomUUID())
                        .requestId(UUID.randomUUID())
                        .createdAt(createdAt)
                        .transferAmount(BigDecimal.valueOf(rand.nextInt(1000) + 1))
                        .originator(originator)
                        .beneficiary(beneficiary)
                        .status(statuses.get(rand.nextInt(statuses.size())))
                        .processedAt(processedAt)
                        .exchangeRate(BigDecimal.ONE)
                        .credit(BigDecimal.valueOf(rand.nextInt(1000) + 1))
                        .debit(BigDecimal.valueOf(rand.nextInt(1000) + 1))
                        .build();

                transferEntities.add(transfer);
            }

            accountRepository.saveAll(accountEntities);
            transferRepository.saveAll(transferEntities);
        };
    }
}