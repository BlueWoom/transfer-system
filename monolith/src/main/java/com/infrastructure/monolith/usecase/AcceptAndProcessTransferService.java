package com.infrastructure.monolith.usecase;

import com.domain.registry.exception.RegistryDomainException;
import com.domain.registry.model.FailedTransfer;
import com.domain.registry.model.SuccessfulTransfer;
import com.domain.registry.usecase.request.FailTransferRequest;
import com.domain.registry.usecase.request.ProcessTransferRequest;
import com.infrastructure.monolith.usecase.registry.FailTransferService;
import com.infrastructure.monolith.usecase.registry.ProcessTransferService;
import com.infrastructure.monolith.usecase.registry.TransferProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AcceptAndProcessTransferService {

    private final ProcessTransferService processTransferService;

    private final FailTransferService failTransferService;

    @Transactional
    public SuccessfulTransfer execute(ProcessTransferRequest request) {
        try {
            return processTransferService.execute(request);
        } catch (RegistryDomainException e) {
            log.error(e.getMessage(), e);
            FailedTransfer failedTransfer = failTransferService.execute(new FailTransferRequest(request.transferId(), request.requestId(), e.getErrorCode()));
            throw new TransferProcessingException(failedTransfer, e.getErrorCode(), e.getMessage(), e);
        }
    }
}
