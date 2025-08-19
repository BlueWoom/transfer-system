package com.infrastructure.transfer_distributed;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Value;
import org.testcontainers.containers.RabbitMQContainer;

abstract class TestcontainerBase {

    static final RabbitMQContainer rabbitMQ = new RabbitMQContainer("rabbitmq:3.12-management");

    @Value("${rabbitmq-config.queue}")
    String queueName;

    @BeforeAll
    static void beforeAll() {
        rabbitMQ.start();
    }
}
