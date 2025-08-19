package com.infrastructure.account_distributed.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq-config.exchange}")
    private String fanoutExchangeName;

    @Value("${rabbitmq-config.dlx}")
    private String deadLetterExchangeName;

    @Value("${rabbitmq-config.dlq}")
    private String deadLetterQueueName;

    @Bean
    public FanoutExchange dataUpdateExchange() {
        return new FanoutExchange(fanoutExchangeName);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(deadLetterExchangeName);
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue(deadLetterQueueName);
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(fanoutExchangeName);
    }

    @Bean
    public Queue instanceUpdateQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", deadLetterExchangeName);
        args.put("x-dead-letter-routing-key", fanoutExchangeName);
        return new AnonymousQueue(args);
    }

    @Bean
    public Binding bindingToDataUpdate(FanoutExchange dataUpdateExchange, Queue instanceUpdateQueue) {
        return BindingBuilder.bind(instanceUpdateQueue).to(dataUpdateExchange);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
