package com.infrastructure.transfer_distributed.api.mapper;

import com.domain.accept.model.AcceptedTransfer;
import com.domain.accept.usecase.request.AcceptTransferRequest;
import com.infrastructure.transfer_distributed.api.dto.TransferDTO;
import com.infrastructure.transfer_distributed.api.dto.TransferRequestDTO;
import com.infrastructure.transfer_distributed.queue.message.TransferRequestMessage;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper
public interface AcceptTransferMapper {

    AcceptTransferMapper INSTANCE = Mappers.getMapper(AcceptTransferMapper.class);

    TransferRequestMessage mapFromModelToMessage(AcceptedTransfer acceptedRequest);

    TransferDTO mapFromModelToDto(AcceptedTransfer acceptedRequest);

    default AcceptTransferRequest mapFromDtoToModel(TransferRequestDTO dto, UUID idempotencyKey) {
        return new AcceptTransferRequest(idempotencyKey, dto.originatorId(), dto.beneficiaryId(), dto.amount());
    }
}
