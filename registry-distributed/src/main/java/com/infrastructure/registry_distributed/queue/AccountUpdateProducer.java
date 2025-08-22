package com.infrastructure.registry_distributed.queue;

import com.domain.registry.model.Account;
import com.infrastructure.registry_distributed.queue.mapper.RegistryMessageMapper;
import com.infrastructure.registry_distributed.queue.message.AccountUpdateMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountUpdateProducer {

    @Value("${rabbitmq-config.account-distributed.exchange}")
    private String accountExchangeName;

    private final RabbitTemplate rabbitTemplate;

    public void sendAccountEvent(Account account) {
        AccountUpdateMessage message = RegistryMessageMapper.INSTANCE.mapFromModelToMessage(account);
        rabbitTemplate.convertAndSend(accountExchangeName, "", message);
    }
}
