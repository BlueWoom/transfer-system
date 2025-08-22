package com.infrastructure.monolith.api.mapper;

import com.domain.accept.model.AcceptedTransfer;
import com.domain.registry.exception.RegistryDomainErrorCode;
import com.domain.registry.model.FailedTransfer;
import com.domain.registry.model.SuccessfulTransfer;
import com.domain.registry.usecase.request.ProcessTransferRequest;
import com.infrastructure.monolith.api.dto.TransferDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RegistryMapper {

    RegistryMapper INSTANCE = Mappers.getMapper(RegistryMapper.class);

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

    ProcessTransferRequest mapFromDtoToModel(AcceptedTransfer acceptedTransfer);
}
