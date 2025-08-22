package com.domain.registry.usecase.request;

import com.domain.registry.exception.RegistryDomainErrorCode;

import java.util.UUID;

public record FailTransferRequest(UUID transferId, RegistryDomainErrorCode errorCode) { }
