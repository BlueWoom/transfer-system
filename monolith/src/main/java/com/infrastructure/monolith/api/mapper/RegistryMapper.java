package com.infrastructure.monolith.api.mapper;

import com.domain.registry.exception.RegistryDomainErrorCode;
import com.domain.registry.model.FailedTransfer;
import com.domain.registry.model.SuccessfulTransfer;
import com.domain.registry.usecase.request.ProcessTransferRequest;
import com.infrastructure.monolith.api.dto.TransferDTO;
import com.infrastructure.monolith.api.dto.TransferRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.OffsetDateTime;
import java.util.UUID;

@Mapper
public interface RegistryMapper {

    RegistryMapper INSTANCE = Mappers.getMapper(RegistryMapper.class);

    default ProcessTransferRequest mapFromDtoToModel(TransferRequestDTO dto, UUID requestId) {
        return new ProcessTransferRequest(UUID.randomUUID(), requestId, OffsetDateTime.now(), dto.originatorId(), dto.beneficiaryId(), dto.amount());
    }

    @Mapping(target = "errorCode", ignore = true)
    @Mapping(target = "status", expression = "java(com.infrastructure.monolith.api.dto.TransferStatusDTO.SUCCESS)")
    TransferDTO mapFromModelToDto(SuccessfulTransfer model);

    @Mapping(target = "transferAmount", ignore = true)
    @Mapping(target = "originator", ignore = true)
    @Mapping(target = "beneficiary", ignore = true)
    @Mapping(target = "exchangeRate", ignore = true)
    @Mapping(target = "debit", ignore = true)
    @Mapping(target = "credit", ignore = true)
    @Mapping(target = "status", expression = "java(com.infrastructure.monolith.api.dto.TransferStatusDTO.FAILED)")
    TransferDTO mapFromModelToDto(FailedTransfer model);

    default String mapFromModelToDTO(RegistryDomainErrorCode registryDomainErrorCode) {
        return registryDomainErrorCode.getValue();
    }
}
