package com.domain.transfer.usecase;

import com.domain.transfer.exception.TransferDomainErrorCode;
import com.domain.transfer.exception.TransferDomainException;
import com.domain.transfer.model.Account;
import com.domain.transfer.model.PendingTransfer;
import com.domain.transfer.port.TransferPort;
import com.domain.transfer.port.query.AccountQuery;
import com.domain.transfer.port.query.TransferQuery;
import com.domain.transfer.usecase.request.TransferRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public abstract class AcceptTransfer implements Usecase<PendingTransfer, TransferRequest> {

    private final TransferPort transferPort;

    @Override
    public PendingTransfer execute(TransferRequest request) {
        // Check transfer request does not exist
        if(transferPort.checkIfRequestExist(new TransferQuery(request.requestId()))) {
            throw new TransferDomainException(TransferDomainErrorCode.DUPLICATED_REQUEST, String.format("Transfer with requestId %s is duplicated", request.requestId()));
        }

        // Perform pre-validation (fail fast)
        Account originator = transferPort.getAccount(new AccountQuery(request.originatorId()))
                .orElseThrow(() -> new TransferDomainException(TransferDomainErrorCode.ACCOUNT_NOT_FOUND, "Originator account not found"));

        Account beneficiary = transferPort.getAccount(new AccountQuery(request.beneficiaryId()))
                .orElseThrow(() -> new TransferDomainException(TransferDomainErrorCode.ACCOUNT_NOT_FOUND, "Beneficiary account not found"));

        // Fetch originator exchange
        BigDecimal exchangeRate = transferPort.getExchangeRate(originator.currency(), beneficiary.currency())
                .orElseThrow(() -> new TransferDomainException(TransferDomainErrorCode.EXCHANGE_RATE_NOT_FOUND, String.format("Exchange rate not found from %s to %s",  originator.currency(), beneficiary.currency())));

        BigDecimal debit = request.amount().multiply(exchangeRate);

        // Validate originator has enough balance
        if (!originator.hasFund(debit)) {
            throw new TransferDomainException(TransferDomainErrorCode.INSUFFICIENT_BALANCE, "Cannot transfer from originator account, insufficient balance");
        }

        // Audit transfer
        PendingTransfer pendingTransfer = new PendingTransfer(
                UUID.randomUUID(),
                request.requestId(),
                OffsetDateTime.now(),
                request.amount(),
                originator,
                beneficiary);

        transferPort.createPendingTransfer(pendingTransfer);
        log.info("Transfer request has been created {}", pendingTransfer);
        return pendingTransfer;
    }
}
