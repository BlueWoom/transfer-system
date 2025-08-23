package com.infrastructure.registry_distributed;

import com.domain.registry.exception.RegistryDomainErrorCode;
import com.domain.registry.model.Account;
import com.infrastructure.registry_distributed.database.entity.AccountEntity;
import com.infrastructure.registry_distributed.database.entity.TransferEntity;
import com.infrastructure.registry_distributed.database.entity.TransferStatus;
import com.infrastructure.registry_distributed.database.repository.AccountService;
import com.infrastructure.registry_distributed.database.repository.TransferService;
import com.infrastructure.registry_distributed.queue.AccountUpdateProducer;
import com.infrastructure.registry_distributed.queue.TransferRequestConsumer;
import com.infrastructure.registry_distributed.queue.message.TransferRequestMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Sql("/test-db/simple-test-data.sql")
class RegistryControllerTest extends RegistryDistributedApplicationTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @MockitoSpyBean
    private TransferRequestConsumer consumer;

    @MockitoSpyBean
    private AccountUpdateProducer producer;

    @Value("${rabbitmq-config.registry-distributed.exchange}")
    private String registryExchange;

    @Value("${rabbitmq-config.registry-distributed.routing-key}")
    private String registryRoutingKey;

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Value("${rabbitmq-config.account-distributed.exchange}")
    private String accountExchangeName;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransferService transferService;

    @Test
    void processTransferSuccessfully() {
        UUID transferId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        Long originatorId = 101L;
        Long beneficiaryId = 102L;
        BigDecimal amount = new BigDecimal("100");
        TransferRequestMessage message = new TransferRequestMessage(transferId, createdAt, originatorId, beneficiaryId, amount);

        rabbitTemplate.convertAndSend(registryExchange, registryRoutingKey, message);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
            verify(producer, times(2)).sendAccountEvent(accountCaptor.capture());
            List<Account> capturedAccounts = accountCaptor.getAllValues();

            Account originatorAccount = capturedAccounts.stream()
                    .filter(acc -> acc.ownerId().equals(originatorId))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Originator account not found in captured events"));

            Account beneficiaryAccount = capturedAccounts.stream()
                    .filter(acc -> acc.ownerId().equals(beneficiaryId))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Beneficiary account not found in captured events"));

            assertThat(originatorAccount.balance()).isEqualByComparingTo("4914.33200");
            assertThat(beneficiaryAccount.balance()).isEqualByComparingTo("2600.00");

            AccountEntity originatorEntity = accountService.findByOwnerId(originatorId).get();
            assertThat(originatorEntity.getBalance()).isEqualByComparingTo("4914.33");

            AccountEntity beneficiaryEntity = accountService.findByOwnerId(beneficiaryId).get();
            assertThat(beneficiaryEntity.getBalance()).isEqualByComparingTo("2600.00");

            Optional<TransferEntity> transferEntityOpt = transferService.getByTransferId(transferId);
            assertThat(transferEntityOpt).isPresent();
            assertThat(transferEntityOpt.get().getTransferId()).isEqualTo(transferId);
            assertThat(transferEntityOpt.get().getCreatedAt()).isCloseTo(createdAt.truncatedTo(ChronoUnit.MICROS), within(1, ChronoUnit.MICROS));
            assertThat(transferEntityOpt.get().getTransferAmount()).isEqualTo(new BigDecimal("100.0000"));
            assertThat(transferEntityOpt.get().getOriginator().getId()).isEqualTo(originatorEntity.getId());
            assertThat(transferEntityOpt.get().getBeneficiary().getId()).isEqualTo(beneficiaryEntity.getId());
            assertThat(transferEntityOpt.get().getStatus()).isEqualTo(TransferStatus.SUCCESS);
            assertThat(transferEntityOpt.get().getProcessedAt()).isNotNull();
            assertThat(transferEntityOpt.get().getExchangeRate()).isEqualTo(new BigDecimal("0.8566800000"));
            assertThat(transferEntityOpt.get().getDebit()).isEqualTo(new BigDecimal("85.6680"));
            assertThat(transferEntityOpt.get().getCredit()).isEqualTo(new BigDecimal("100.0000"));
        });
    }

    @Test
    void shouldFailIfTransferOriginatorIsNotFound() {
        UUID transferId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        Long originatorId = 666L;
        Long beneficiaryId = 102L;
        BigDecimal amount = new BigDecimal("100");
        TransferRequestMessage message = new TransferRequestMessage(transferId, createdAt, originatorId, beneficiaryId, amount);

        rabbitTemplate.convertAndSend(registryExchange, registryRoutingKey, message);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            AccountEntity beneficiaryEntity = accountService.findByOwnerId(beneficiaryId).get();
            assertThat(beneficiaryEntity.getBalance()).isEqualByComparingTo("2500.00");

            Optional<TransferEntity> transferEntityOpt = transferService.getByTransferId(transferId);
            assertFailedMessage(transferEntityOpt, transferId, createdAt, RegistryDomainErrorCode.ACCOUNT_NOT_FOUND);
        });
    }

    @Test
    void shouldFailIfTransferBeneficiaryIsNotFound() {
        UUID transferId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        Long originatorId = 101L;
        Long beneficiaryId = 666L;
        BigDecimal amount = new BigDecimal("100");
        TransferRequestMessage message = new TransferRequestMessage(transferId, createdAt, originatorId, beneficiaryId, amount);

        rabbitTemplate.convertAndSend(registryExchange, registryRoutingKey, message);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            AccountEntity originatorEntity = accountService.findByOwnerId(originatorId).get();
            assertThat(originatorEntity.getBalance()).isEqualByComparingTo("5000.00");

            Optional<TransferEntity> transferEntityOpt = transferService.getByTransferId(transferId);
            assertFailedMessage(transferEntityOpt, transferId, createdAt, RegistryDomainErrorCode.ACCOUNT_NOT_FOUND);
        });
    }

    @Test
    void shouldFailIfTransferAmountIsZero() {
        UUID transferId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        Long originatorId = 101L;
        Long beneficiaryId = 102L;
        BigDecimal amount = new BigDecimal("0");
        TransferRequestMessage message = new TransferRequestMessage(transferId, createdAt, originatorId, beneficiaryId, amount);

        rabbitTemplate.convertAndSend(registryExchange, registryRoutingKey, message);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            AccountEntity beneficiaryEntity = accountService.findByOwnerId(beneficiaryId).get();
            assertThat(beneficiaryEntity.getBalance()).isEqualByComparingTo("2500.00");

            AccountEntity originatorEntity = accountService.findByOwnerId(originatorId).get();
            assertThat(originatorEntity.getBalance()).isEqualByComparingTo("5000.00");

            Optional<TransferEntity> transferEntityOpt = transferService.getByTransferId(transferId);
            assertFailedMessage(transferEntityOpt, transferId, createdAt, RegistryDomainErrorCode.NEGATIVE_AMOUNT);
        });
    }

    @Test
    void shouldFailIfTransferAmountIsNegative() {
        UUID transferId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        Long originatorId = 101L;
        Long beneficiaryId = 102L;
        BigDecimal amount = new BigDecimal("-100");
        TransferRequestMessage message = new TransferRequestMessage(transferId, createdAt, originatorId, beneficiaryId, amount);

        rabbitTemplate.convertAndSend(registryExchange, registryRoutingKey, message);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            AccountEntity beneficiaryEntity = accountService.findByOwnerId(beneficiaryId).get();
            assertThat(beneficiaryEntity.getBalance()).isEqualByComparingTo("2500.00");

            AccountEntity originatorEntity = accountService.findByOwnerId(originatorId).get();
            assertThat(originatorEntity.getBalance()).isEqualByComparingTo("5000.00");

            Optional<TransferEntity> transferEntityOpt = transferService.getByTransferId(transferId);
            assertFailedMessage(transferEntityOpt, transferId, createdAt, RegistryDomainErrorCode.NEGATIVE_AMOUNT);
        });
    }

    @Test
    void shouldFailIfTransferHasSameOriginatorAndBeneficiary() {
        UUID transferId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        Long originatorId = 101L;
        Long beneficiaryId = 101L;
        BigDecimal amount = new BigDecimal("0");
        TransferRequestMessage message = new TransferRequestMessage(transferId, createdAt, originatorId, beneficiaryId, amount);

        rabbitTemplate.convertAndSend(registryExchange, registryRoutingKey, message);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            AccountEntity beneficiaryEntity = accountService.findByOwnerId(beneficiaryId).get();
            assertThat(beneficiaryEntity.getBalance()).isEqualByComparingTo("5000.00");

            Optional<TransferEntity> transferEntityOpt = transferService.getByTransferId(transferId);
            assertFailedMessage(transferEntityOpt, transferId, createdAt, RegistryDomainErrorCode.INVALID_BENEFICIARY);
        });
    }

    @Test
    void shouldFailIfBalanceIsInsufficient() {
        UUID transferId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        Long originatorId = 101L;
        Long beneficiaryId = 102L;
        BigDecimal amount = new BigDecimal("10000");
        TransferRequestMessage message = new TransferRequestMessage(transferId, createdAt, originatorId, beneficiaryId, amount);

        rabbitTemplate.convertAndSend(registryExchange, registryRoutingKey, message);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            AccountEntity beneficiaryEntity = accountService.findByOwnerId(beneficiaryId).get();
            assertThat(beneficiaryEntity.getBalance()).isEqualByComparingTo("2500.00");

            AccountEntity originatorEntity = accountService.findByOwnerId(originatorId).get();
            assertThat(originatorEntity.getBalance()).isEqualByComparingTo("5000.00");

            Optional<TransferEntity> transferEntityOpt = transferService.getByTransferId(transferId);
            assertFailedMessage(transferEntityOpt, transferId, createdAt, RegistryDomainErrorCode.INSUFFICIENT_BALANCE);
        });
    }

    @Test
    void shouldFailIfExchangeRateIsNotFound() {
        UUID transferId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        Long originatorId = 101L;
        Long beneficiaryId = 104L;
        BigDecimal amount = new BigDecimal("0");
        TransferRequestMessage message = new TransferRequestMessage(transferId, createdAt, originatorId, beneficiaryId, amount);

        rabbitTemplate.convertAndSend(registryExchange, registryRoutingKey, message);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            AccountEntity beneficiaryEntity = accountService.findByOwnerId(beneficiaryId).get();
            assertThat(beneficiaryEntity.getBalance()).isEqualByComparingTo("10000.00");

            AccountEntity originatorEntity = accountService.findByOwnerId(originatorId).get();
            assertThat(originatorEntity.getBalance()).isEqualByComparingTo("5000.00");

            Optional<TransferEntity> transferEntityOpt = transferService.getByTransferId(transferId);
            assertFailedMessage(transferEntityOpt, transferId, createdAt, RegistryDomainErrorCode.EXCHANGE_RATE_NOT_FOUND);
        });
    }

    private static void assertFailedMessage(Optional<TransferEntity> transferEntityOpt, UUID transferId, OffsetDateTime createdAt, RegistryDomainErrorCode errorCode) {
        assertThat(transferEntityOpt).isPresent();
        assertThat(transferEntityOpt.get().getTransferId()).isEqualTo(transferId);
        assertThat(transferEntityOpt.get().getCreatedAt()).isCloseTo(createdAt.withOffsetSameInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS), within(1, ChronoUnit.SECONDS));
        assertThat(transferEntityOpt.get().getTransferAmount()).isNull();
        assertThat(transferEntityOpt.get().getOriginator()).isNull();
        assertThat(transferEntityOpt.get().getBeneficiary()).isNull();
        assertThat(transferEntityOpt.get().getStatus()).isEqualTo(TransferStatus.FAILED);
        assertThat(transferEntityOpt.get().getProcessedAt()).isNotNull();
        assertThat(transferEntityOpt.get().getExchangeRate()).isNull();
        assertThat(transferEntityOpt.get().getDebit()).isNull();
        assertThat(transferEntityOpt.get().getCredit()).isNull();
        assertThat(transferEntityOpt.get().getErrorCode()).isEqualTo(errorCode.getValue());
    }
}
