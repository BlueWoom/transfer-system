package com.infrastructure.registry_distributed.queue.mapper;

import com.domain.registry.model.Account;
import com.domain.registry.usecase.request.ProcessTransferRequest;
import com.infrastructure.registry_distributed.queue.message.AccountUpdateMessage;
import com.infrastructure.registry_distributed.queue.message.TransferRequestMessage;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RegistryMessageMapper {

    RegistryMessageMapper INSTANCE = Mappers.getMapper(RegistryMessageMapper.class);

    AccountUpdateMessage mapFromModelToMessage(Account account);

    ProcessTransferRequest mapMessageToModel(TransferRequestMessage message);
}
