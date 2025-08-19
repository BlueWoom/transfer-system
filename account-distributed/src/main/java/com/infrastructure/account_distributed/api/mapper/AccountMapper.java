package com.infrastructure.account_distributed.api.mapper;

import com.domain.account.model.Account;
import com.infrastructure.account_distributed.api.dto.AccountDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountMapper {

    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    AccountDTO mapFromModelToDto(Account account);
}
