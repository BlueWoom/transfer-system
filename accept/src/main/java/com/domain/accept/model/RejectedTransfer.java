package com.domain.accept.model;

import java.util.UUID;

public record RejectedTransfer(UUID transferId, UUID requestId) { }
