package com.domain.transfer.usecase;

import com.domain.transfer.exception.TransferDomainErrorCode;
import com.domain.transfer.exception.TransferDomainException;
import com.domain.transfer.model.*;
import com.domain.transfer.port.AccountPort;
import com.domain.transfer.port.ExchangePort;
import com.domain.transfer.port.TransferPort;
import com.domain.transfer.port.query.AccountQuery;
import com.domain.transfer.port.query.ProcessTransferQuery;
import com.domain.transfer.usecase.request.ProcessTransferRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Slf4j
@RequiredArgsConstructor
public abstract class ProcessTransfer implements Usecase<SuccessTransfer, ProcessTransferRequest> {

    private final AccountPort accountPort;

    private final ExchangePort exchangePort;

    private final TransferPort transferPort;

    @Override
    public SuccessTransfer execute(ProcessTransferRequest request) {
        // Check if pending request exists and fetch it
        PendingTransfer transfer = transferPort.getPendingTransferForUpdate(new ProcessTransferQuery(request.transferId()))
                .orElseThrow(() -> new TransferDomainException(TransferDomainErrorCode.TRANSFER_NOT_FOUND, String.format("Transfer with transferId %s and status PENDING not found", request.transferId())));

        // Validate account exist and fetch them for update
        Account originator = accountPort.getAccountByIdForUpdate(new AccountQuery(transfer.getOriginator().getOwnerId()))
                .orElseThrow(() -> new TransferDomainException(TransferDomainErrorCode.ACCOUNT_NOT_FOUND, "Originator account not found"));

        Account beneficiary = accountPort.getAccountByIdForUpdate(new AccountQuery(transfer.getBeneficiary().getOwnerId()))
                .orElseThrow(() -> new TransferDomainException(TransferDomainErrorCode.ACCOUNT_NOT_FOUND, "Beneficiary account not found"));

        // Fetch originator exchange
        BigDecimal exchangeRate = exchangePort.getExchangeRate(originator.getCurrency(), beneficiary.getCurrency())
                .orElseThrow(() -> new TransferDomainException(TransferDomainErrorCode.EXCHANGE_RATE_NOT_FOUND, String.format("Exchange rate not found from %s to %s",  originator.getCurrency(), beneficiary.getCurrency())));

        BigDecimal debit = transfer.getTransferAmount().multiply(exchangeRate);

        // Validate originator has enough balance
        if (!originator.hasFund(debit)) {
            throw new TransferDomainException(TransferDomainErrorCode.INSUFFICIENT_BALANCE, "Cannot transfer from originator account");
        }

        BigDecimal credit = transfer.getTransferAmount();

        // Audit transfer
        Account updatedOriginator = originator.debit(debit);
        Account updatedBeneficiary = beneficiary.credit(credit);
        SuccessTransfer successTransfer = new SuccessTransfer(transfer, OffsetDateTime.now(), exchangeRate, debit, credit);
        accountPort.save(updatedOriginator);
        accountPort.save(updatedBeneficiary);
        transferPort.save(successTransfer);

        log.info("Transfer request {} has been processed SUCCESSFULLY", successTransfer);

        return successTransfer;
    }
}
