package com.domain.transfer.port;

import com.domain.transfer.model.FailedTransfer;
import com.domain.transfer.model.PendingTransfer;
import com.domain.transfer.model.SuccessTransfer;
import com.domain.transfer.port.query.ProcessTransferQuery;
import com.domain.transfer.port.query.TransferQuery;

import java.util.Optional;

public interface TransferPort {

    boolean checkIfRequestExist(TransferQuery transferQuery);

    Optional<PendingTransfer> getPendingTransferForUpdate(ProcessTransferQuery processTransferQuery);

    Optional<PendingTransfer> getPendingTransferForUpdate(TransferQuery transferQuery);

    void save(PendingTransfer transfer);

    void save(SuccessTransfer transfer);

    void save(FailedTransfer transfer);
}
