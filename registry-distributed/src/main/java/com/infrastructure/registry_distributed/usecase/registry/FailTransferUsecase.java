package com.infrastructure.registry_distributed.usecase.registry;

import com.domain.registry.model.FailedTransfer;
import com.domain.registry.usecase.FailTransfer;
import com.domain.registry.usecase.request.FailTransferRequest;
import com.infrastructure.registry_distributed.database.repository.TransferService;
import com.infrastructure.registry_distributed.usecase.registry.mapper.RegistryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FailTransferUsecase extends FailTransfer {

    private final TransferService transferService;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public FailedTransfer execute(FailTransferRequest request) {
        FailedTransfer failTransfer = super.execute(request);
        transferService.save(RegistryMapper.INSTANCE.mapFromModelToEntity(failTransfer));
        return failTransfer;
    }
}
