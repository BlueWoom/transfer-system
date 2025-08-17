package com.domain.registry.model;

import com.domain.registry.exception.RegistryDomainErrorCode;
import com.domain.registry.exception.RegistryDomainException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum Currency {

    AUD("AUD"),
    BGN("BGN"),
    BRL("BRL"),
    CAD("CAD"),
    CHF("CHF"),
    CNY("CNY"),
    CZK("CZK"),
    DKK("DKK"),
    EUR("EUR"),
    GBP("GBP"),
    HKD("HKD"),
    HUF("HUF"),
    IDR("IDR"),
    ILS("ILS"),
    INR("INR"),
    ISK("ISK"),
    JPY("JPY"),
    KRW("KRW"),
    MXN("MXN"),
    MYR("MYR"),
    NOK("NOK"),
    NZD("NZD"),
    PHP("PHP"),
    PLN("PLN"),
    RON("RON"),
    SEK("SEK"),
    SGD("SGD"),
    THB("THB"),
    TRY("TRY"),
    USD("USD"),
    ZAR("ZAR");

    private final String value;

    public static Currency fromValue(String value) {
        return Stream.of(Currency.values())
                .filter(c -> c.getValue().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new RegistryDomainException(RegistryDomainErrorCode.INVALID_CURRENCY, String.format("Unknown currency %s", value)));
    }
}
