package com.infrastructure.transfer_distributed.api.mapper;

import com.domain.accept.model.AcceptedTransfer;
import com.domain.accept.usecase.request.AcceptTransferRequest;
import com.infrastructure.transfer_distributed.api.dto.TransferDTO;
import com.infrastructure.transfer_distributed.api.dto.TransferRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper
public interface AcceptTransferMapper {

    AcceptTransferMapper INSTANCE = Mappers.getMapper(AcceptTransferMapper.class);

    default AcceptTransferRequest mapFromDtoToModel(TransferRequestDTO dto, UUID idempotencyKey) {
        return AcceptTransferRequest.builder()
                .requestId(idempotencyKey)
                .originatorId(dto.originatorId())
                .beneficiaryId(dto.beneficiaryId())
                .amount(dto.amount())
                .build();
    }

    TransferDTO mapFromModelToDto(AcceptedTransfer acceptedRequest);
}
