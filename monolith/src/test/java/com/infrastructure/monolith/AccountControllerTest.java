package com.infrastructure.monolith;

import com.domain.account.exception.AccountDomainErrorCode;
import com.infrastructure.monolith.api.dto.AccountDTO;
import com.infrastructure.monolith.api.dto.ErrorDTO;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Sql("/test-db/simple-test-data.sql")
class AccountControllerTest extends MonolithApplicationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getAccountSuccessfully() {
        String url = "/account/{ownerId}";
        ResponseEntity<AccountDTO> response = restTemplate.getForEntity(url, AccountDTO.class, 101L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        AccountDTO accountDTO = response.getBody();
        assertThat(accountDTO).isNotNull();
        assertThat(accountDTO.ownerId()).isEqualTo(101L);
        assertThat(accountDTO.currency()).isEqualTo("EUR");
        assertThat(accountDTO.balance()).isEqualTo(new BigDecimal("5000.00"));
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
}
