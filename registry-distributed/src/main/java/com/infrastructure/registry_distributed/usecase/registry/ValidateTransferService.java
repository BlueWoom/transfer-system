package com.infrastructure.registry_distributed.usecase.registry;

import com.domain.registry.model.SuccessfulTransfer;
import com.domain.registry.port.RegistryPort;
import com.domain.registry.usecase.ValidateTransfer;
import com.domain.registry.usecase.request.ValidateTransferRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ValidateTransferService extends ValidateTransfer {

    public ValidateTransferService(RegistryPort registryPort) {
        super(registryPort);
    }

    @Override
    @Transactional
    public SuccessfulTransfer execute(ValidateTransferRequest request) {
        return super.execute(request);
    }
}