package com.infrastructure.monolith;

import com.domain.registry.exception.RegistryDomainErrorCode;
import com.infrastructure.monolith.api.dto.AccountDTO;
import com.infrastructure.monolith.api.dto.TransferDTO;
import com.infrastructure.monolith.api.dto.TransferRequestDTO;
import com.infrastructure.monolith.api.dto.TransferStatusDTO;
import com.infrastructure.monolith.database.entity.AccountEntity;
import com.infrastructure.monolith.database.entity.TransferEntity;
import com.infrastructure.monolith.database.entity.TransferStatus;
import com.infrastructure.monolith.database.repository.AccountService;
import com.infrastructure.monolith.database.repository.TransferService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Disabled
@Sql("/test-db/simple-test-data.sql")
class RegistryControllerTest extends MonolithApplicationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransferService transferService;

    @Test
    void processTransferSuccessfully() {
        TransferRequestDTO transferRequest = new TransferRequestDTO(101L, 102L, new BigDecimal("1000"));

        HttpHeaders headers = new HttpHeaders();
        UUID idempotentKey = UUID.randomUUID();
        headers.set("Idempotency-Key", idempotentKey.toString());
        HttpEntity<TransferRequestDTO> requestEntity = new HttpEntity<>(transferRequest, headers);

        ResponseEntity<TransferDTO> response = restTemplate.postForEntity("/transfer", requestEntity, TransferDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Assert transfer DTO is as expected
        TransferDTO actualTransfer = response.getBody();
        assertThat(actualTransfer.requestId()).isEqualTo(idempotentKey);
        assertThat(actualTransfer.transferAmount()).isEqualTo(new BigDecimal("1000"));
        assertThat(actualTransfer.originator()).isEqualTo(new AccountDTO(101L, "EUR", new BigDecimal("4143.32000")));
        assertThat(actualTransfer.beneficiary()).isEqualTo(new AccountDTO(102L, "USD", new BigDecimal("3500.00")));
        assertThat(actualTransfer.status()).isEqualTo(TransferStatusDTO.SUCCESS);
        assertThat(actualTransfer.exchangeRate()).isEqualTo(new BigDecimal("0.85668"));
        assertThat(actualTransfer.debit()).isEqualTo(new BigDecimal("856.68000"));
        assertThat(actualTransfer.credit()).isEqualTo(new BigDecimal("1000"));

        // Assert database status is as expected
        AccountEntity originatorEntity = accountService.findByOwnerId(101L).get();
        assertThat(originatorEntity.getBalance()).isEqualTo(new BigDecimal("4143.32"));

        AccountEntity beneficiaryEntity = accountService.findByOwnerId(102L).get();
        assertThat(beneficiaryEntity.getBalance()).isEqualTo(new BigDecimal("3500.00"));

        Optional<TransferEntity> transferEntity = transferService.getByTransferId(actualTransfer.transferId());
        assertThat(transferEntity).isPresent();
        assertSuccessfulTransfer(transferEntity.get(), originatorEntity, beneficiaryEntity, actualTransfer);
    }

    @Test
    void shouldFailIfRequestIsDuplicated() {
        TransferRequestDTO transferRequest = new TransferRequestDTO(101L, 102L, new BigDecimal("1000"));

        HttpHeaders headers = new HttpHeaders();
        String idempotentKey = "d3c4b5a6-9870-6543-2109-876fedcba321";
        headers.set("Idempotency-Key", idempotentKey);
        HttpEntity<TransferRequestDTO> requestEntity = new HttpEntity<>(transferRequest, headers);

        ResponseEntity<TransferDTO> response = restTemplate.postForEntity("/transfer", requestEntity, TransferDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();

        // Assert actualTransfer DTO is as expected
        TransferDTO actualTransfer = response.getBody();
        assertThat(actualTransfer.status()).isEqualTo(TransferStatusDTO.FAILED);
        assertThat(actualTransfer.errorCode()).isEqualTo(RegistryDomainErrorCode.DUPLICATED_REQUEST.getValue());

        // Assert database status is as expected
        Optional<TransferEntity> transferEntity = transferService.getByTransferId(actualTransfer.transferId());
        assertThat(transferEntity).isPresent();
        assertFailedTransfer(transferEntity.get(), actualTransfer);
    }

    @Test
    void shouldFailIfTransferOriginatorIsNotFound() {
        TransferRequestDTO transferRequest = new TransferRequestDTO(666L, 102L, new BigDecimal("1000"));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Idempotency-Key", UUID.randomUUID().toString());
        HttpEntity<TransferRequestDTO> requestEntity = new HttpEntity<>(transferRequest, headers);

        ResponseEntity<TransferDTO> response = restTemplate.postForEntity("/transfer", requestEntity, TransferDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();

        // Assert actualTransfer DTO is as expected
        TransferDTO actualTransfer = response.getBody();
        assertThat(actualTransfer.status()).isEqualTo(TransferStatusDTO.FAILED);
        assertThat(actualTransfer.errorCode()).isEqualTo(RegistryDomainErrorCode.ACCOUNT_NOT_FOUND.getValue());

        // Assert database status is as expected
        Optional<TransferEntity> transferEntity = transferService.getByTransferId(actualTransfer.transferId());
        assertThat(transferEntity).isPresent();
        assertFailedTransfer(transferEntity.get(), actualTransfer);
    }

    @Test
    void shouldFailIfTransferBeneficiaryIsNotFound() {
        TransferRequestDTO transferRequest = new TransferRequestDTO(101L, 666L, new BigDecimal("1000"));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Idempotency-Key", UUID.randomUUID().toString());
        HttpEntity<TransferRequestDTO> requestEntity = new HttpEntity<>(transferRequest, headers);

        ResponseEntity<TransferDTO> response = restTemplate.postForEntity("/transfer", requestEntity, TransferDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();

        // Assert actualTransfer DTO is as expected
        TransferDTO actualTransfer = response.getBody();
        assertThat(actualTransfer.status()).isEqualTo(TransferStatusDTO.FAILED);
        assertThat(actualTransfer.errorCode()).isEqualTo(RegistryDomainErrorCode.ACCOUNT_NOT_FOUND.getValue());

        // Assert database status is as expected
        Optional<TransferEntity> transferEntity = transferService.getByTransferId(actualTransfer.transferId());
        assertThat(transferEntity).isPresent();
        assertFailedTransfer(transferEntity.get(), actualTransfer);
    }

    @Test
    void shouldFailIfTransferAmountIsZero() {
        TransferRequestDTO transferRequest = new TransferRequestDTO(101L, 102L, new BigDecimal("0"));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Idempotency-Key", UUID.randomUUID().toString());
        HttpEntity<TransferRequestDTO> requestEntity = new HttpEntity<>(transferRequest, headers);

        ResponseEntity<TransferDTO> response = restTemplate.postForEntity("/transfer", requestEntity, TransferDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();

        // Assert actualTransfer DTO is as expected
        TransferDTO actualTransfer = response.getBody();
        assertThat(actualTransfer.status()).isEqualTo(TransferStatusDTO.FAILED);
        assertThat(actualTransfer.errorCode()).isEqualTo(RegistryDomainErrorCode.NEGATIVE_AMOUNT.getValue());

        // Assert database status is as expected
        Optional<TransferEntity> transferEntity = transferService.getByTransferId(actualTransfer.transferId());
        assertThat(transferEntity).isPresent();
        assertFailedTransfer(transferEntity.get(), actualTransfer);
    }

    @Test
    void shouldFailIfTransferAmountIsNegative() {
        TransferRequestDTO transferRequest = new TransferRequestDTO(101L, 102L, new BigDecimal("-1000"));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Idempotency-Key", UUID.randomUUID().toString());
        HttpEntity<TransferRequestDTO> requestEntity = new HttpEntity<>(transferRequest, headers);

        ResponseEntity<TransferDTO> response = restTemplate.postForEntity("/transfer", requestEntity, TransferDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();

        // Assert actualTransfer DTO is as expected
        TransferDTO actualTransfer = response.getBody();
        assertThat(actualTransfer.status()).isEqualTo(TransferStatusDTO.FAILED);
        assertThat(actualTransfer.errorCode()).isEqualTo(RegistryDomainErrorCode.NEGATIVE_AMOUNT.getValue());

        // Assert database status is as expected
        Optional<TransferEntity> transferEntity = transferService.getByTransferId(actualTransfer.transferId());
        assertThat(transferEntity).isPresent();
        assertFailedTransfer(transferEntity.get(), actualTransfer);
    }

    @Test
    void shouldFailIfTransferHasSameOriginatorAndBeneficiary() {
        TransferRequestDTO transferRequest = new TransferRequestDTO(101L, 101L, new BigDecimal("1000"));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Idempotency-Key", UUID.randomUUID().toString());
        HttpEntity<TransferRequestDTO> requestEntity = new HttpEntity<>(transferRequest, headers);

        ResponseEntity<TransferDTO> response = restTemplate.postForEntity("/transfer", requestEntity, TransferDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();

        // Assert actualTransfer DTO is as expected
        TransferDTO actualTransfer = response.getBody();
        assertThat(actualTransfer.status()).isEqualTo(TransferStatusDTO.FAILED);
        assertThat(actualTransfer.errorCode()).isEqualTo(RegistryDomainErrorCode.INVALID_BENEFICIARY.getValue());

        // Assert database status is as expected
        Optional<TransferEntity> transferEntity = transferService.getByTransferId(actualTransfer.transferId());
        assertThat(transferEntity).isPresent();
        assertFailedTransfer(transferEntity.get(), actualTransfer);
    }

    @Test
    void shouldFailIfBalanceIsInsufficient() {
        TransferRequestDTO transferRequest = new TransferRequestDTO(101L, 102L, new BigDecimal("100000"));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Idempotency-Key", UUID.randomUUID().toString());
        HttpEntity<TransferRequestDTO> requestEntity = new HttpEntity<>(transferRequest, headers);

        ResponseEntity<TransferDTO> response = restTemplate.postForEntity("/transfer", requestEntity, TransferDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();

        // Assert actualTransfer DTO is as expected
        TransferDTO actualTransfer = response.getBody();
        assertThat(actualTransfer.status()).isEqualTo(TransferStatusDTO.FAILED);
        assertThat(actualTransfer.errorCode()).isEqualTo(RegistryDomainErrorCode.INSUFFICIENT_BALANCE.getValue());

        // Assert database status is as expected
        Optional<TransferEntity> transferEntity = transferService.getByTransferId(actualTransfer.transferId());
        assertThat(transferEntity).isPresent();
        assertFailedTransfer(transferEntity.get(), actualTransfer);
    }

    @Test
    void shouldFailIfExchangeRateIsNotFound() {
        TransferRequestDTO transferRequest = new TransferRequestDTO(101L, 104L, new BigDecimal("100"));

        HttpHeaders headers = new HttpHeaders();
        UUID idempotentKey = UUID.randomUUID();
        headers.set("Idempotency-Key", idempotentKey.toString());
        HttpEntity<TransferRequestDTO> requestEntity = new HttpEntity<>(transferRequest, headers);

        ResponseEntity<TransferDTO> response = restTemplate.postForEntity("/transfer", requestEntity, TransferDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();

        // Assert actualTransfer DTO is as expected
        TransferDTO actualTransfer = response.getBody();
        assertThat(actualTransfer.status()).isEqualTo(TransferStatusDTO.FAILED);
        assertThat(actualTransfer.errorCode()).isEqualTo(RegistryDomainErrorCode.EXCHANGE_RATE_NOT_FOUND.getValue());

        // Assert database status is as expected
        Optional<TransferEntity> transferEntity = transferService.getByTransferId(actualTransfer.transferId());
        assertThat(transferEntity).isPresent();
        assertFailedTransfer(transferEntity.get(), actualTransfer);
    }

    private void assertSuccessfulTransfer(TransferEntity transferEntity, AccountEntity originatorEntity, AccountEntity beneficiaryEntity, TransferDTO actualTransfer) {
        assertThat(transferEntity.getTransferId()).isEqualTo(actualTransfer.transferId());
        assertThat(transferEntity.getRequestId()).isEqualTo(actualTransfer.requestId());
        assertThat(transferEntity.getCreatedAt()).isCloseTo(actualTransfer.createdAt().truncatedTo(ChronoUnit.MICROS), within(1, ChronoUnit.MICROS));
        assertThat(transferEntity.getTransferAmount()).isEqualTo(new BigDecimal("1000.0000"));
        assertThat(transferEntity.getOriginator().getId()).isEqualTo(originatorEntity.getId());
        assertThat(transferEntity.getBeneficiary().getId()).isEqualTo(beneficiaryEntity.getId());
        assertThat(transferEntity.getStatus()).isEqualTo(TransferStatus.SUCCESS);
        assertThat(transferEntity.getProcessedAt()).isCloseTo(actualTransfer.processedAt().truncatedTo(ChronoUnit.MICROS), within(1, ChronoUnit.MICROS));
        assertThat(transferEntity.getExchangeRate()).isEqualTo(new BigDecimal("0.8566800000"));
        assertThat(transferEntity.getDebit()).isEqualTo(new BigDecimal("856.6800"));
        assertThat(transferEntity.getCredit()).isEqualTo(new BigDecimal("1000.0000"));
    }

    private void assertFailedTransfer(TransferEntity transferEntity, TransferDTO actualTransfer) {
        assertThat(transferEntity.getTransferId()).isEqualTo(actualTransfer.transferId());
        assertThat(transferEntity.getRequestId()).isEqualTo(actualTransfer.requestId());
        assertThat(transferEntity.getCreatedAt()).isCloseTo(actualTransfer.createdAt().truncatedTo(ChronoUnit.MICROS), within(1, ChronoUnit.MICROS));
        assertThat(transferEntity.getStatus().toString()).isEqualTo(actualTransfer.status().toString());
        assertThat(transferEntity.getProcessedAt()).isCloseTo(actualTransfer.processedAt().truncatedTo(ChronoUnit.MICROS), within(1, ChronoUnit.MICROS));
        assertThat(transferEntity.getErrorCode()).isEqualTo(actualTransfer.errorCode());

        assertThat(transferEntity.getTransferAmount()).isNull();
        assertThat(transferEntity.getOriginator()).isNull();
        assertThat(transferEntity.getBeneficiary()).isNull();
        assertThat(transferEntity.getExchangeRate()).isNull();
        assertThat(transferEntity.getDebit()).isNull();
        assertThat(transferEntity.getCredit()).isNull();
    }
}
