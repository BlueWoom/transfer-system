package com.infrastructure.account_distributed.queue;

import com.infrastructure.account_distributed.queue.mapper.UpdateAccountMessageMapper;
import com.infrastructure.account_distributed.queue.message.AccountUpdateMessage;
import com.infrastructure.account_distributed.usecase.account.UpdateAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateAccountConsumer {

    private final UpdateAccountService updateAccount;

    @RabbitListener(queues = "#{instanceUpdateQueue.name}")
    public void handleDataUpdate(AccountUpdateMessage message) {
        updateAccount.execute(UpdateAccountMessageMapper.INSTANCE.mapFromMessageToModel(message));
    }
}
