package com.infrastructure.monolith.usecase.accept;

import com.domain.accept.model.RejectedTransfer;
import com.domain.accept.port.AcceptPort;
import com.domain.accept.usecase.RejectTransfer;
import com.domain.accept.usecase.request.AcceptTransferRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RejectTransferUsecase extends RejectTransfer {

    public RejectTransferUsecase(AcceptPort acceptPort) {
        super(acceptPort);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public RejectedTransfer execute(AcceptTransferRequest request) {
        return super.execute(request);
    }
}
