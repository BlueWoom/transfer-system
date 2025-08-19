package com.infrastructure.account_distributed;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;

abstract class TestcontainerBase {

    public static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    static final RabbitMQContainer rabbitMQ = new RabbitMQContainer("rabbitmq:3.12-management");

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq-config.exchange}")
    String fanoutExchangeName;

    @Value("${rabbitmq-config.dlq}")
    String deadLetterQueueName;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
        rabbitMQ.start();
    }
}
