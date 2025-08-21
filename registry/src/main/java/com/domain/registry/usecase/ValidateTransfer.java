package com.domain.registry.usecase;

import com.domain.registry.exception.RegistryDomainErrorCode;
import com.domain.registry.exception.RegistryDomainException;
import com.domain.registry.model.Account;
import com.domain.registry.model.SuccessfulTransfer;
import com.domain.registry.port.RegistryPort;
import com.domain.registry.port.query.TransferRequestQuery;
import com.domain.registry.usecase.request.ValidateTransferRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Slf4j
@RequiredArgsConstructor
public abstract class ValidateTransfer implements Usecase<SuccessfulTransfer, ValidateTransferRequest> {

    private final RegistryPort registryPort;

    @Override
    public SuccessfulTransfer execute(ValidateTransferRequest request) {
        // Check transfer request does not exist
        if(registryPort.checkIfRequestExist(new TransferRequestQuery(request.requestId()))) {
            throw new RegistryDomainException(RegistryDomainErrorCode.DUPLICATED_REQUEST, String.format("Transfer with requestId %s is duplicated", request.requestId()));
        }

        Account originator = request.originator();
        Account beneficiary = request.beneficiary();

        // Fetch originator exchange
        BigDecimal exchangeRate = registryPort.getExchangeRate(originator.currency(), beneficiary.currency())
                .orElseThrow(() -> new RegistryDomainException(RegistryDomainErrorCode.EXCHANGE_RATE_NOT_FOUND, String.format("Exchange rate not found from %s to %s",  originator.currency(), beneficiary.currency())));

        if (exchangeRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new RegistryDomainException(RegistryDomainErrorCode.EXCHANGE_RATE_NEGATIVE, String.format("Exchange rate from %s to %s is negative: %s", originator.currency(), beneficiary.currency(), exchangeRate));
        }

        BigDecimal debit = request.amount().multiply(exchangeRate);
        BigDecimal credit = request.amount();

        // Update account balance
        Account updatedOriginator = originator.debit(debit);
        Account updatedBeneficiary = beneficiary.credit(credit);

        // Create successful transfer
        return new SuccessfulTransfer(request.transferId(), request.requestId(), request.createdAt(),
                request.amount(), updatedOriginator, updatedBeneficiary, OffsetDateTime.now(), exchangeRate, debit, credit);
    }
}
