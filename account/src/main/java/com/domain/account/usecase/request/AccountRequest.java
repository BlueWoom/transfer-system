package com.domain.account.usecase.request;

import lombok.Builder;

@Builder
public record AccountRequest(Long ownerId) { }
