package com.infrastructure.monolith.api.mapper;

import com.domain.account.model.Account;
import com.infrastructure.monolith.api.dto.AccountDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DomainAccountMapper {

    DomainAccountMapper INSTANCE = Mappers.getMapper(DomainAccountMapper.class);

    AccountDTO mapFromModelToDto(Account account);
}
