package com.domain.registry.usecase;

import com.domain.registry.exception.RegistryDomainErrorCode;
import com.domain.registry.exception.RegistryDomainException;
import com.domain.registry.model.Account;
import com.domain.registry.model.SuccessfulTransfer;
import com.domain.registry.port.RegistryPort;
import com.domain.registry.port.query.AccountQuery;
import com.domain.registry.port.query.TransferRequestQuery;
import com.domain.registry.usecase.request.ProcessTransferRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Slf4j
@RequiredArgsConstructor
public abstract class ProcessTransfer implements Usecase<SuccessfulTransfer, ProcessTransferRequest> {

    private final RegistryPort registryPort;

    @Override
    public SuccessfulTransfer execute(ProcessTransferRequest request) {
        // Check transfer request does not exist
        if(registryPort.checkIfRequestExist(new TransferRequestQuery(request.requestId()))) {
            throw new RegistryDomainException(RegistryDomainErrorCode.DUPLICATED_REQUEST, String.format("Transfer with requestId %s is duplicated", request.requestId()));
        }

        // Validate account exist and fetch them for update
        Account originator = registryPort.getAccountByIdForUpdate(new AccountQuery(request.originatorId()))
                .orElseThrow(() -> new RegistryDomainException(RegistryDomainErrorCode.ACCOUNT_NOT_FOUND, "Originator account not found"));

        Account beneficiary = registryPort.getAccountByIdForUpdate(new AccountQuery(request.beneficiaryId()))
                .orElseThrow(() -> new RegistryDomainException(RegistryDomainErrorCode.ACCOUNT_NOT_FOUND, "Beneficiary account not found"));

        // Fetch originator exchange
        BigDecimal exchangeRate = registryPort.getExchangeRate(originator.currency(), beneficiary.currency())
                .orElseThrow(() -> new RegistryDomainException(RegistryDomainErrorCode.EXCHANGE_RATE_NOT_FOUND, String.format("Exchange rate not found from %s to %s",  originator.currency(), beneficiary.currency())));

        BigDecimal debit = request.amount().multiply(exchangeRate);

        BigDecimal credit = request.amount();

        // Update account balance
        Account updatedOriginator = originator.debit(debit);
        Account updatedBeneficiary = beneficiary.credit(credit);
        registryPort.updateAccount(updatedOriginator);
        registryPort.updateAccount(updatedBeneficiary);

        // Create successful transfer
        SuccessfulTransfer successfulTransfer = new SuccessfulTransfer(request.transferId(), request.requestId(), request.createdAt(),
                request.amount(), updatedOriginator, updatedBeneficiary, OffsetDateTime.now(), exchangeRate, debit, credit);

        registryPort.createSuccessfulTransfer(successfulTransfer);

        log.info("Transfer request {} has been processed SUCCESSFULLY", successfulTransfer);

        return successfulTransfer;
    }
}
