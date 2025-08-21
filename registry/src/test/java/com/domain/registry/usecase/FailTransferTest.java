package com.domain.registry.usecase;

import com.domain.registry.exception.RegistryDomainErrorCode;
import com.domain.registry.model.FailedTransfer;
import com.domain.registry.usecase.request.FailTransferRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FailTransferTest {

    private FailTransfer failTransfer;

    @BeforeEach
    void setUp() {
        failTransfer = new FailTransfer() { };
    }

    @Test
    void shouldFailTransferSuccessfully() {
        FailTransferRequest request = new FailTransferRequest(UUID.randomUUID(), UUID.randomUUID(), RegistryDomainErrorCode.ACCOUNT_NOT_FOUND);

        FailedTransfer result = failTransfer.execute(request);

        assertThat(result).isNotNull();
        assertThat(result.getRequestId()).isEqualTo(request.requestId());
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getProcessedAt()).isNotNull();
    }
}
