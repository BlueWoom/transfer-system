package com.infrastructure.registry_distributed.queue;

import com.domain.registry.model.SuccessfulTransfer;
import com.domain.registry.usecase.ProcessTransfer;
import com.domain.registry.usecase.request.ProcessTransferRequest;
import com.infrastructure.registry_distributed.queue.mapper.RegistryMessageMapper;
import com.infrastructure.registry_distributed.queue.message.TransferRequestMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransferRequestConsumer {

    private final ProcessTransfer processTransfer;

    private final AccountUpdateProducer accountUpdateProducer;

    @RabbitListener(queues = "${rabbitmq-config.registry-distributed.queue}")
    public void handleMessage(TransferRequestMessage message) {
        ProcessTransferRequest request = RegistryMessageMapper.INSTANCE.mapMessageToModel(message);
        SuccessfulTransfer successfulTransfer = processTransfer.execute(request);
        accountUpdateProducer.sendAccountEvent(successfulTransfer.getOriginator());
        accountUpdateProducer.sendAccountEvent(successfulTransfer.getBeneficiary());
    }
}
