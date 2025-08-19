package com.infrastructure.transfer_distributed.api.mapper;

import com.infrastructure.transfer_distributed.api.dto.TransferDTO;
import com.infrastructure.transfer_distributed.queue.message.TransferRequestMessage;
import com.infrastructure.transfer_distributed.api.dto.TransferRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.OffsetDateTime;
import java.util.UUID;

@Mapper
public interface RegistryMapper {

    RegistryMapper INSTANCE = Mappers.getMapper(RegistryMapper.class);

    default TransferRequestMessage mapFromDtoToMessage(TransferRequestDTO dto, UUID requestId) {
        return new TransferRequestMessage(UUID.randomUUID(), requestId, OffsetDateTime.now(), dto.originatorId(), dto.beneficiaryId(), dto.amount());
    }

    TransferDTO mapFromMessageToDTO(TransferRequestMessage transferRequestMessage);
}
