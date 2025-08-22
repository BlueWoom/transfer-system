package com.infrastructure.account_distributed;

import com.domain.account.exception.AccountDomainErrorCode;
import com.infrastructure.account_distributed.api.dto.AccountDTO;
import com.infrastructure.account_distributed.api.dto.ErrorDTO;
import com.infrastructure.account_distributed.database.entity.AccountEntity;
import com.infrastructure.account_distributed.database.repository.AccountService;
import com.infrastructure.account_distributed.queue.message.AccountUpdateMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;

@Sql("/test-db/simple-test-data.sql")
class AccountControllerTest extends AccountDistributedApplicationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AccountService accountService;

    @Test
    void getAccountSuccessfully() {
        String url = "/account/{ownerId}";
        ResponseEntity<AccountDTO> response = restTemplate.getForEntity(url, AccountDTO.class, 101L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        AccountDTO accountDTO = response.getBody();
        assertThat(accountDTO).isNotNull();
        assertThat(accountDTO.ownerId()).isEqualTo(101L);
        assertThat(accountDTO.currency()).isEqualTo("EUR");
        assertThat(accountDTO.balance()).isEqualTo(new BigDecimal("1000.00"));
    }

    @Test
    void getAllAccountsSuccessfully() {
        String url = "/accounts?page=0&size=10";
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.get("content")).isNotNull();
        assertThat(responseBody.get("page")).isNotNull();
    }

    @Test
    void getAccountNotFound() {
        String url = "/account/{ownerId}";
        ResponseEntity<ErrorDTO> response = restTemplate.getForEntity(url, ErrorDTO.class, 666L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ErrorDTO errorBody = response.getBody();
        assertThat(errorBody).isNotNull();
        assertThat(errorBody.getErrorCode()).isEqualTo(AccountDomainErrorCode.ACCOUNT_NOT_FOUND.getValue());
        assertThat(errorBody.getMessage()).isEqualTo("Account with ownerId 666 not found");
        assertThat(errorBody.getTimestamp()).isNotNull();
        assertThat(errorBody.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    void whenMessageIsPublished_thenListenerShouldReceiveIt() {
        AccountUpdateMessage message = new AccountUpdateMessage(101L, new BigDecimal("666.00"));
        rabbitTemplate.convertAndSend(fanoutExchangeName, "", message);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            Optional<AccountEntity> account = accountService.findByOwnerId(message.ownerId());

            assertThat(account).isPresent();
            assertThat(account.get().getBalance()).isEqualByComparingTo(new BigDecimal("666.00"));
        });
    }

    @Test
    void whenListenerFails_thenMessageGoesToDLQ() {
        AccountUpdateMessage failingMessage = new AccountUpdateMessage(666L, new BigDecimal("1000.00"));
        rabbitTemplate.convertAndSend(fanoutExchangeName, "", failingMessage);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            AccountUpdateMessage messageFromDLQ = (AccountUpdateMessage) rabbitTemplate.receiveAndConvert(deadLetterQueueName);
            assertThat(messageFromDLQ).isNotNull();
            assertThat(messageFromDLQ).isEqualTo(failingMessage);
        });
    }
}
