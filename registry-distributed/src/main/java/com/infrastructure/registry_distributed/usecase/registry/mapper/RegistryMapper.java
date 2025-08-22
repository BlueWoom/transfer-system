package com.infrastructure.registry_distributed.usecase.registry.mapper;

import com.domain.registry.exception.RegistryDomainErrorCode;
import com.domain.registry.model.Account;
import com.domain.registry.model.Currency;
import com.domain.registry.model.FailedTransfer;
import com.domain.registry.model.SuccessfulTransfer;
import com.infrastructure.registry_distributed.database.entity.AccountEntity;
import com.infrastructure.registry_distributed.database.entity.TransferEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RegistryMapper {

    RegistryMapper INSTANCE = Mappers.getMapper(RegistryMapper.class);

    @Mapping(target = "debit", ignore = true)
    @Mapping(target = "credit", ignore = true)
    @Mapping(target = "currency", source = "currency", qualifiedByName = "mapCurrency")
    Account mapFromEntityToModel(AccountEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    AccountEntity mapFromModelToEntity(Account model);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "status", expression = "java(com.infrastructure.registry_distributed.database.entity.TransferStatus.SUCCESS)")
    @Mapping(target = "errorCode", ignore = true)
    TransferEntity mapFromModelToEntity(SuccessfulTransfer transfer);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "transferAmount", ignore = true)
    @Mapping(target = "originator", ignore = true)
    @Mapping(target = "beneficiary", ignore = true)
    @Mapping(target = "exchangeRate", ignore = true)
    @Mapping(target = "debit", ignore = true)
    @Mapping(target = "credit", ignore = true)
    @Mapping(target = "status", expression = "java(com.infrastructure.registry_distributed.database.entity.TransferStatus.FAILED)")
    @Mapping(target="errorCode", source="errorCode", qualifiedByName = "mapErrorCode")
    TransferEntity mapFromModelToEntity(FailedTransfer transfer);

    @Named("mapCurrency")
    default Currency mapFromStringToModel(String currency) {
        return Currency.valueOf(currency);
    }

    @Named("mapErrorCode")
    default String mapFromModelToString(RegistryDomainErrorCode errorCode) {
        return errorCode.getValue();
    }
}
