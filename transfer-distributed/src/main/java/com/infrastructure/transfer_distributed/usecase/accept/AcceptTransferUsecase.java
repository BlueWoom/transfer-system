package com.infrastructure.transfer_distributed.usecase.accept;

import com.domain.accept.exception.AcceptDomainException;
import com.domain.accept.model.AcceptedTransfer;
import com.domain.accept.model.RejectedTransfer;
import com.domain.accept.port.AcceptPort;
import com.domain.accept.usecase.AcceptTransfer;
import com.domain.accept.usecase.RejectTransfer;
import com.domain.accept.usecase.request.AcceptTransferRequest;
import com.infrastructure.transfer_distributed.database.repository.RequestService;
import com.infrastructure.transfer_distributed.queue.TransferRequestProducer;
import com.infrastructure.transfer_distributed.usecase.accept.mapper.AcceptTransferMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AcceptTransferUsecase extends AcceptTransfer {

    private final RequestService requestService;

    private final RejectTransfer rejectTransfer;

    private final TransferRequestProducer transferRequestProducer;

    public AcceptTransferUsecase(AcceptPort acceptPort, RequestService requestService, RejectTransfer rejectTransfer, TransferRequestProducer transferRequestProducer) {
        super(acceptPort);
        this.requestService = requestService;
        this.rejectTransfer = rejectTransfer;
        this.transferRequestProducer = transferRequestProducer;
    }

    @Override
    @Transactional
    public AcceptedTransfer execute(AcceptTransferRequest request) {
        try {
            AcceptedTransfer acceptedTransfer = super.execute(request);
            requestService.save(AcceptTransferMapper.INSTANCE.mapFromModelToEntity(acceptedTransfer));
            transferRequestProducer.sendTransferRequest(AcceptTransferMapper.INSTANCE.mapFromModelToMessage(acceptedTransfer));
            log.error("Request accepted: {}", acceptedTransfer);
            return acceptedTransfer;
        } catch (AcceptDomainException e) {
            RejectedTransfer rejectedTransfer = rejectTransfer.execute(request);
            log.error("Duplicated request: {}", rejectedTransfer);
            throw new AcceptTransferException(rejectedTransfer, e.getErrorCode(), e.getMessage(), e);
        }
    }
}
