package com.infrastructure.transfer_distributed.queue;

import com.infrastructure.transfer_distributed.queue.message.TransferRequestMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferRequestProducer {

    @Value("${rabbitmq-config.exchange}")
    private String exchangeName;

    @Value("${rabbitmq-config.routing-key}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;

    public void sendTransferRequest(TransferRequestMessage message) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
    }
}
