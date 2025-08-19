package com.infrastructure.transfer_distributed.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq-config.exchange}")
    private String exchangeName;

    @Value("${rabbitmq-config.queue}")
    private String queueName;

    @Value("${rabbitmq-config.routing-key}")
    private String routingKey;

    @Value("${rabbitmq-config.dlx}")
    private String deadLetterExchangeName;

    @Value("${rabbitmq-config.dlq}")
    private String deadLetterQueueName;

    @Bean
    DirectExchange registryExchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange(deadLetterExchangeName);
    }

    @Bean
    Queue deadLetterQueue() {
        return QueueBuilder.durable(deadLetterQueueName).build();
    }

    @Bean
    Binding deadLetterBinding() {
        // Use the main routing key for the DLQ binding as well
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(routingKey);
    }

    @Bean
    Queue registryQueue() {
        return QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", deadLetterExchangeName)
                .withArgument("x-dead-letter-routing-key", routingKey)
                .build();
    }

    @Bean
    Binding registryBinding() {
        return BindingBuilder.bind(registryQueue()).to(registryExchange()).with(routingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}