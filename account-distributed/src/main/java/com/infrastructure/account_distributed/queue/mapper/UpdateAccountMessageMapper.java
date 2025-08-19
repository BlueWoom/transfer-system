package com.infrastructure.account_distributed.queue.mapper;

import com.domain.account.usecase.request.AccountUpdateRequest;
import com.infrastructure.account_distributed.queue.message.UpdateAccountMessage;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UpdateAccountMessageMapper {

    UpdateAccountMessageMapper INSTANCE = Mappers.getMapper(UpdateAccountMessageMapper.class);

    AccountUpdateRequest mapFromMessageToModel(UpdateAccountMessage message);
}
