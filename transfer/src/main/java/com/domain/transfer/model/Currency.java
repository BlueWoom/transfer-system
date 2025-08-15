package com.domain.transfer.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Currency {

    EUR("EUR"),
    USD("USD");

    private final String value;
}
