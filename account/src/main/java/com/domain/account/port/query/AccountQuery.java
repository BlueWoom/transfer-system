package com.domain.account.port.query;

import lombok.Builder;

@Builder
public record AccountQuery(Long ownerId) { }
