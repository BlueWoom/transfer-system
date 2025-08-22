package com.infrastructure.transfer_distributed;

import com.infrastructure.transfer_distributed.api.dto.ErrorDTO;
import com.infrastructure.transfer_distributed.api.dto.TransferDTO;
import com.infrastructure.transfer_distributed.api.dto.TransferRequestDTO;
import com.infrastructure.transfer_distributed.queue.message.TransferRequestMessage;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Sql("/test-db/simple-test-data.sql")
class TransferControllerTest extends TransferDistributedApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void sendTransferRequestMessageSuccessfully() {
        TransferRequestDTO transferRequest = new TransferRequestDTO(101L, 102L, new BigDecimal("1000"));

        HttpHeaders headers = new HttpHeaders();
        UUID idempotentKey = UUID.randomUUID();
        headers.set("Idempotency-Key", idempotentKey.toString());
        HttpEntity<TransferRequestDTO> requestEntity = new HttpEntity<>(transferRequest, headers);

        ResponseEntity<TransferDTO> response = restTemplate.postForEntity("/send-request-transfer", requestEntity, TransferDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Assert transfer DTO is as expected
        TransferDTO actualTransfer = response.getBody();
        assertThat(actualTransfer.requestId()).isEqualTo(idempotentKey);

        // Assert sent message is as expected
        Object messageFromQueue = rabbitTemplate.receiveAndConvert(queueName, 2000);

        assertThat(messageFromQueue).isInstanceOf(TransferRequestMessage.class);
        TransferRequestMessage receivedRequest = (TransferRequestMessage) messageFromQueue;

        assertThat(receivedRequest.transferId()).isEqualTo(actualTransfer.transferId());
        assertThat(receivedRequest.requestId()).isEqualTo(idempotentKey);
        assertThat(receivedRequest.createdAt()).isCloseTo(actualTransfer.createdAt().truncatedTo(ChronoUnit.MICROS), within(1, ChronoUnit.MICROS));
        assertThat(receivedRequest.amount()).isEqualTo(transferRequest.amount());
        assertThat(receivedRequest.originatorId()).isEqualTo(transferRequest.originatorId());
        assertThat(receivedRequest.beneficiaryId()).isEqualTo(transferRequest.beneficiaryId());
    }

    @Test
    void sendDuplicatedTransferRequestMessage() {
        TransferRequestDTO transferRequest = new TransferRequestDTO(101L, 102L, new BigDecimal("1000"));

        HttpHeaders headers = new HttpHeaders();
        String idempotentKey = "d3c4b5a6-9870-6543-2109-876fedcba321";
        headers.set("Idempotency-Key", idempotentKey.toString());
        HttpEntity<TransferRequestDTO> requestEntity = new HttpEntity<>(transferRequest, headers);

        ResponseEntity<ErrorDTO> response = restTemplate.postForEntity("/send-request-transfer", requestEntity, ErrorDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("Duplicated request");
        assertThat(response.getBody().getMessage()).isEqualTo("Transfer with requestId d3c4b5a6-9870-6543-2109-876fedcba321 is duplicated");
        assertThat(response.getBody().getTransactionId()).isEqualTo(UUID.fromString("a1b2c3d4-e5f6-7890-1234-567890abcdef"));
        assertThat(response.getBody().getRequestId()).isEqualTo(UUID.fromString(idempotentKey));
        assertThat(response.getBody().getTimestamp()).isNotNull();
        assertThat(response.getBody().getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
    }
}
