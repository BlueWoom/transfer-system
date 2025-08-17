package com.infrastructure.monolith.usecase.registry;

import com.domain.registry.model.SuccessfulTransfer;
import com.domain.registry.port.RegistryPort;
import com.domain.registry.usecase.ProcessTransfer;
import com.domain.registry.usecase.request.ProcessTransferRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProcessTransferService extends ProcessTransfer {

    public ProcessTransferService(RegistryPort registryPort) {
        super(registryPort);
    }

    @Override
    @Transactional
    public SuccessfulTransfer execute(ProcessTransferRequest request) {
        return super.execute(request);
    }
}