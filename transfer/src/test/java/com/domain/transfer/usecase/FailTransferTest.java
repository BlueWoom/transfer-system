package com.domain.transfer.usecase;

import com.domain.transfer.model.*;
import com.domain.transfer.port.TransferPort;
import com.domain.transfer.port.query.TransferQuery;
import com.domain.transfer.usecase.request.FailTransferRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class FailTransferTest {

    private TransferPort transferPort;

    private FailTransfer usecase;

    @BeforeEach
    void setUp() {
        transferPort = mock(TransferPort.class);
        usecase = new FailTransfer(transferPort) {
        };
    }

    @Test
    void should_fail_transfer_successfully() {
        UUID requestId = UUID.randomUUID();
        Account originator = new Account(1L, Currency.EUR, new BigDecimal("200.0"));
        Account beneficiary = new Account(2L, Currency.USD, new BigDecimal("300.0"));

        PendingTransfer pendingTransfer = new PendingTransfer(UUID.randomUUID(), requestId, OffsetDateTime.now(), new BigDecimal("100.0"), originator, beneficiary);

        when(transferPort.getPendingTransferForUpdate(new TransferQuery(requestId)))
                .thenReturn(Optional.of(pendingTransfer));

        FailTransferRequest request = new FailTransferRequest(requestId);

        FailedTransfer actualTransfer = usecase.execute(request);

        FailedTransfer expectedTransfer = new FailedTransfer(pendingTransfer, OffsetDateTime.now());

        assertThat(actualTransfer)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "processedAt")
                .isEqualTo(expectedTransfer);

        ArgumentCaptor<FailedTransfer> captor = ArgumentCaptor.forClass(FailedTransfer.class);
        verify(transferPort).save(captor.capture());
        assertThat(captor.getValue().getRequestId()).isEqualTo(requestId);
        assertThat(captor.getValue().getProcessedAt()).isNotNull();
        assertThat(captor.getValue().getOriginator()).isEqualTo(originator);
        assertThat(captor.getValue().getBeneficiary()).isEqualTo(beneficiary);
        assertThat(captor.getValue().getTransferAmount()).isEqualTo(pendingTransfer.getTransferAmount());
    }

    @Test
    void should_create_failed_transfer_when_not_found() {
        UUID requestId = UUID.randomUUID();
        when(transferPort.getPendingTransferForUpdate(new TransferQuery(requestId)))
                .thenReturn(Optional.empty());

        FailTransferRequest request = new FailTransferRequest(requestId);

        FailedTransfer actualTransfer = usecase.execute(request);

        assertThat(actualTransfer.getRequestId()).isEqualTo(requestId);

        ArgumentCaptor<FailedTransfer> captor = ArgumentCaptor.forClass(FailedTransfer.class);
        verify(transferPort).save(captor.capture());
        assertThat(actualTransfer.getRequestId()).isEqualTo(requestId);
        assertThat(actualTransfer.getProcessedAt()).isNotNull();
        assertThat(actualTransfer.getOriginator()).isNull();
        assertThat(actualTransfer.getBeneficiary()).isNull();
        assertThat(actualTransfer.getTransferAmount()).isNull();
    }
}