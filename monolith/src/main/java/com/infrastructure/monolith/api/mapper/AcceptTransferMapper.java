package com.infrastructure.monolith.api.mapper;

import com.domain.accept.usecase.request.AcceptTransferRequest;
import com.infrastructure.monolith.api.dto.TransferRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper
public interface AcceptTransferMapper {

    AcceptTransferMapper INSTANCE = Mappers.getMapper(AcceptTransferMapper.class);

    default AcceptTransferRequest mapFromDtoToModel(TransferRequestDTO dto, UUID requestId) {
        return new AcceptTransferRequest(requestId, dto.originatorId(), dto.beneficiaryId(), dto.amount());
    }
}
