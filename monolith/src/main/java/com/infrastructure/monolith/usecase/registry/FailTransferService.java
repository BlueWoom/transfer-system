package com.infrastructure.monolith.usecase.registry;

import com.domain.registry.model.FailedTransfer;
import com.domain.registry.port.RegistryPort;
import com.domain.registry.usecase.FailTransfer;
import com.domain.registry.usecase.request.FailTransferRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FailTransferService extends FailTransfer {

    public FailTransferService(RegistryPort registryPort) {
        super(registryPort);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public FailedTransfer execute(FailTransferRequest request) {
        return super.execute(request);
    }
}
