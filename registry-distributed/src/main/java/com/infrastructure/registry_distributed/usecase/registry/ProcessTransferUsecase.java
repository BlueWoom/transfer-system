package com.infrastructure.registry_distributed.usecase.registry;

import com.domain.registry.exception.RegistryDomainErrorCode;
import com.domain.registry.exception.RegistryDomainException;
import com.domain.registry.model.FailedTransfer;
import com.domain.registry.model.SuccessfulTransfer;
import com.domain.registry.usecase.FailTransfer;
import com.domain.registry.usecase.ProcessTransfer;
import com.domain.registry.usecase.ValidateTransfer;
import com.domain.registry.usecase.request.FailTransferRequest;
import com.domain.registry.usecase.request.ProcessTransferRequest;
import com.domain.registry.usecase.request.ValidateTransferRequest;
import com.infrastructure.registry_distributed.database.entity.AccountEntity;
import com.infrastructure.registry_distributed.database.entity.TransferEntity;
import com.infrastructure.registry_distributed.database.repository.AccountService;
import com.infrastructure.registry_distributed.database.repository.TransferService;
import com.infrastructure.registry_distributed.usecase.registry.mapper.RegistryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessTransferUsecase extends ProcessTransfer {

    private final ValidateTransfer validateTransfer;

    private final FailTransfer failTransfer;

    private final AccountService accountService;

    private final TransferService transferService;

    @Override
    @Transactional
    public SuccessfulTransfer execute(ProcessTransferRequest request) {
        try {
            // Fetch LOCK and ensure we don't lose this references
            AccountEntity originator = accountService.findByOwnerIdForUpdate(request.originatorId())
                    .orElseThrow(() -> new RegistryDomainException(RegistryDomainErrorCode.ACCOUNT_NOT_FOUND, "Originator account not found"));

            AccountEntity beneficiary = accountService.findByOwnerIdForUpdate(request.beneficiaryId())
                    .orElseThrow(() -> new RegistryDomainException(RegistryDomainErrorCode.ACCOUNT_NOT_FOUND, "Beneficiary account not found"));

            ValidateTransferRequest validate = ValidateTransferRequest.builder()
                    .transferId(request.transferId())
                    .createdAt(request.createdAt())
                    .originator(RegistryMapper.INSTANCE.mapFromEntityToModel(originator))
                    .beneficiary(RegistryMapper.INSTANCE.mapFromEntityToModel(beneficiary))
                    .amount(request.amount())
                    .build();

            SuccessfulTransfer successfulTransfer = validateTransfer.execute(validate);

            originator.setBalance(successfulTransfer.getOriginator().balance());
            beneficiary.setBalance(successfulTransfer.getBeneficiary().balance());
            AccountEntity updatedOriginator = accountService.save(originator);
            AccountEntity updatedBeneficiary = accountService.save(beneficiary);

            TransferEntity transfer = RegistryMapper.INSTANCE.mapFromModelToEntity(successfulTransfer);
            transfer.setOriginator(updatedOriginator);
            transfer.setBeneficiary(updatedBeneficiary);
            transferService.save(transfer);

            log.info("Transfer {} has been processed SUCCESSFULLY", successfulTransfer);
            return successfulTransfer;
        } catch (RegistryDomainException e) {
            FailTransferRequest failTransferRequest = FailTransferRequest.builder()
                    .transferId(request.transferId())
                    .errorCode(e.getErrorCode())
                    .build();

            FailedTransfer failedTransfer = failTransfer.execute(failTransferRequest);
            log.error("Transfer {} has failed", failedTransfer);
            throw new AmqpRejectAndDontRequeueException("Invalid message data: " + e.getMessage());
        } catch (Exception e) {
            log.error("Transfer {} has failed", request, e);
            throw new AmqpRejectAndDontRequeueException("Invalid message data: " + e.getMessage());
        }
    }
}
