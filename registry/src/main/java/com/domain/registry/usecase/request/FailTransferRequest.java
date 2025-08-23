package com.domain.registry.usecase.request;

import com.domain.registry.exception.RegistryDomainErrorCode;
import lombok.Builder;

import java.util.UUID;

@Builder
public record FailTransferRequest(UUID transferId,
                                  RegistryDomainErrorCode errorCode) { }
