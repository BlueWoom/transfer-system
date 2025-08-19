package com.domain.registry.usecase;

import com.domain.registry.exception.RegistryDomainErrorCode;
import com.domain.registry.model.FailedTransfer;
import com.domain.registry.port.RegistryPort;
import com.domain.registry.usecase.request.FailTransferRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

class FailTransferTest {

    private RegistryPort registryPort;

    private FailTransfer failTransfer;

    @BeforeEach
    void setUp() {
        registryPort = Mockito.mock(RegistryPort.class);
        failTransfer = new FailTransfer(registryPort) { };
    }

    @Test
    void shouldFailTransferSuccessfully() {
        FailTransferRequest request = new FailTransferRequest(UUID.randomUUID(), UUID.randomUUID(), RegistryDomainErrorCode.ACCOUNT_NOT_FOUND);

        FailedTransfer result = failTransfer.execute(request);

        ArgumentCaptor<FailedTransfer> failedTransferCaptor = ArgumentCaptor.forClass(FailedTransfer.class);
        verify(registryPort).createFailedTransfer(failedTransferCaptor.capture());

        FailedTransfer capturedTransfer = failedTransferCaptor.getValue();
        assertThat(result).isNotNull();
        assertThat(result.getRequestId()).isEqualTo(request.requestId());
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getProcessedAt()).isNotNull();
        assertThat(capturedTransfer.getRequestId()).isEqualTo(request.requestId());
    }
}
