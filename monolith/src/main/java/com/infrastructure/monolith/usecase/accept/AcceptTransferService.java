package com.infrastructure.monolith.usecase.accept;

import com.domain.accept.exception.AcceptDomainException;
import com.domain.accept.model.AcceptedTransfer;
import com.domain.accept.model.RejectedTransfer;
import com.domain.accept.port.AcceptPort;
import com.domain.accept.usecase.AcceptTransfer;
import com.domain.accept.usecase.request.AcceptTransferRequest;
import com.infrastructure.monolith.database.repository.RequestService;
import com.infrastructure.monolith.usecase.accept.mapper.AcceptTransferMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AcceptTransferService extends AcceptTransfer {

    private final RequestService requestService;

    private final RejectTransferService rejectTransferService;

    public AcceptTransferService(AcceptPort acceptPort, RequestService requestService, RejectTransferService rejectTransferService) {
        super(acceptPort);
        this.requestService = requestService;
        this.rejectTransferService = rejectTransferService;
    }

    @Override
    @Transactional
    public AcceptedTransfer execute(AcceptTransferRequest request) {
        try {
            AcceptedTransfer acceptedTransfer = super.execute(request);
            requestService.save(AcceptTransferMapper.INSTANCE.mapFromModelToEntity(acceptedTransfer));
            log.error("Request accepted: {}", acceptedTransfer);
            return acceptedTransfer;
        } catch (AcceptDomainException e) {
            RejectedTransfer rejectedTransfer = rejectTransferService.execute(request);
            log.error("Duplicated request: {}", rejectedTransfer);
            throw new AcceptTransferException(rejectedTransfer, e.getErrorCode(), e.getMessage(), e);
        }
    }
}
