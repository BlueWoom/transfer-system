package com.domain.accept.model;

import lombok.Builder;

import java.util.UUID;

@Builder
public record RejectedTransfer(UUID transferId,
                               UUID requestId) { }
