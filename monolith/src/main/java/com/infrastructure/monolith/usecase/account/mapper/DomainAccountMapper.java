package com.infrastructure.monolith.usecase.account.mapper;

import com.domain.account.model.Account;
import com.infrastructure.monolith.database.entity.AccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DomainAccountMapper {

    DomainAccountMapper INSTANCE = Mappers.getMapper(DomainAccountMapper.class);

    Account mapFromEntityToModel(AccountEntity entity);
}
