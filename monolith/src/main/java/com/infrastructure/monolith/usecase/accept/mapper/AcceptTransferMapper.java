package com.infrastructure.monolith.usecase.accept.mapper;

import com.domain.accept.model.AcceptedTransfer;
import com.domain.accept.model.RejectedTransfer;
import com.infrastructure.monolith.database.entity.RequestEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AcceptTransferMapper {

    AcceptTransferMapper INSTANCE = Mappers.getMapper(AcceptTransferMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    RequestEntity mapFromModelToEntity(AcceptedTransfer transfer);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    RequestEntity mapFromModelToEntity(RejectedTransfer transfer);
}
