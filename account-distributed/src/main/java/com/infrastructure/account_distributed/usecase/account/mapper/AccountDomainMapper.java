package com.infrastructure.account_distributed.usecase.account.mapper;

import com.domain.account.model.Account;
import com.infrastructure.account_distributed.database.entity.AccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountDomainMapper {

    AccountDomainMapper INSTANCE = Mappers.getMapper(AccountDomainMapper.class);

    Account mapFromEntityToModel(AccountEntity entity);
}
