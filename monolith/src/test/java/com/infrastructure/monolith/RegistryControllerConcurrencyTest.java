package com.infrastructure.monolith;

import com.infrastructure.monolith.api.dto.TransferDTO;
import com.infrastructure.monolith.api.dto.TransferRequestDTO;
import com.infrastructure.monolith.database.entity.AccountEntity;
import com.infrastructure.monolith.database.entity.TransferEntity;
import com.infrastructure.monolith.database.entity.TransferStatus;
import com.infrastructure.monolith.database.repository.AccountRepository;
import com.infrastructure.monolith.database.repository.TransferRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Sql("/test-db/concurrency-test-data.sql")
class RegistryControllerConcurrencyTest extends MonolithApplicationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TransferRepository transferRepository;

    static final Integer MAX_NUMBER_OF_TRANSFER = 5000;

    // Remove: @Lock(LockModeType.PESSIMISTIC_WRITE) this see this test probably failing

    @Test
    void processConcurrentTransfersSuccessfully() throws InterruptedException {
        List<TransferRequestDTO> transferRequests = List.of(
                new TransferRequestDTO(101L, 102L, new BigDecimal("1")),
                new TransferRequestDTO(103L, 102L, new BigDecimal("1"))
        );

        List<TransferDTO> transfers = Collections.synchronizedList(new ArrayList<>());

        CountDownLatch latch = new CountDownLatch(transferRequests.size() * MAX_NUMBER_OF_TRANSFER);
        ExecutorService executorService = Executors.newFixedThreadPool(transferRequests.size() * MAX_NUMBER_OF_TRANSFER);

        for (int i = 0; i < MAX_NUMBER_OF_TRANSFER; i++) {
            transferRequests.forEach(transferRequest ->
                    executorService.submit(() -> {
                        try {
                            HttpHeaders headers = new HttpHeaders();
                            headers.set("Idempotency-Key", UUID.randomUUID().toString());
                            HttpEntity<TransferRequestDTO> requestEntity = new HttpEntity<>(transferRequest, headers);
                            ResponseEntity<TransferDTO> response = restTemplate.postForEntity("/transfer", requestEntity, TransferDTO.class);
                            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                            assertThat(response.getBody()).isNotNull();
                            transfers.add(response.getBody());
                        } finally {
                            latch.countDown();
                        }
                    })
            );
        }

        latch.await();
        executorService.shutdown();

        transfers.forEach(transfer -> {
            Optional<TransferEntity> transferEntity = transferRepository.getByTransferId(transfer.transferId());
            assertThat(transferEntity).isPresent();
            assertThat(transferEntity.get().getStatus()).isEqualTo(TransferStatus.SUCCESS);
        });

        AccountEntity originator1 = accountRepository.findByOwnerId(101L).get();
        AccountEntity originator2 = accountRepository.findByOwnerId(103L).get();
        AccountEntity beneficiary = accountRepository.findByOwnerId(102L).get();
        assertThat(originator1.getBalance()).isEqualTo(new BigDecimal("0.00"));
        assertThat(originator2.getBalance()).isEqualTo(new BigDecimal("0.00"));
        assertThat(beneficiary.getBalance()).isEqualTo(new BigDecimal("11000.00"));
    }

}
