package com.infrastructure.registry_distributed.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Producer Infrastructure: "account-distributed"

    @Bean
    public FanoutExchange accountFanoutExchange(@Value("${rabbitmq-config.account-distributed.exchange}") String exchangeName) {
        return new FanoutExchange(exchangeName);
    }

    // Consumer Infrastructure: "registry-distributed"

    @Bean
    public DirectExchange registryExchange(@Value("${rabbitmq-config.registry-distributed.exchange}") String exchangeName) {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Queue registryQueue(@Value("${rabbitmq-config.registry-distributed.queue}") String queueName, @Value("${rabbitmq-config.registry-distributed.dlx}") String dlxName, @Value("${rabbitmq-config.registry-distributed.routing-key}") String routingKey) {
        // Configure the main queue to use the dead-letter exchange
        return QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", dlxName)
                .withArgument("x-dead-letter-routing-key", routingKey)
                .build();
    }

    @Bean
    public Binding registryBinding(Queue registryQueue, DirectExchange registryExchange, @Value("${rabbitmq-config.registry-distributed.routing-key}") String routingKey) {
        return BindingBuilder.bind(registryQueue)
                .to(registryExchange)
                .with(routingKey);
    }

    // Dead-Letter Infrastructure for the Consumer

    @Bean
    public DirectExchange registryDlx(@Value("${rabbitmq-config.registry-distributed.dlx}") String dlxName) {
        return new DirectExchange(dlxName);
    }

    @Bean
    public Queue registryDlq(@Value("${rabbitmq-config.registry-distributed.dlq}") String dlqName) {
        return new Queue(dlqName);
    }

    @Bean
    public Binding registryDlxBinding(Queue registryDlq, DirectExchange registryDlx, @Value("${rabbitmq-config.registry-distributed.routing-key}") String originalRoutingKey) {
        return BindingBuilder.bind(registryDlq)
                .to(registryDlx)
                .with(originalRoutingKey);
    }
}
