package com.domain.registry.model;

import com.domain.registry.exception.RegistryDomainErrorCode;
import com.domain.registry.exception.RegistryDomainException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CurrencyTest {

    @Test
    void fromValueShouldReturnCurrencyForValidUpperCaseValue() {
        String currencyCode = "USD";
        Currency result = Currency.fromValue(currencyCode);
        assertNotNull(result);
        assertEquals(Currency.USD, result);
        assertEquals("USD", result.getValue());
    }

    @Test
    void fromValueShouldReturnCurrencyForValidLowerCaseValue() {
        String currencyCode = "eur";
        Currency result = Currency.fromValue(currencyCode);
        assertNotNull(result);
        assertEquals(Currency.EUR, result);
    }

    @Test
    void fromValueShouldReturnCurrencyForValidMixedCaseValue() {
        String currencyCode = "jPy";
        Currency result = Currency.fromValue(currencyCode);
        assertNotNull(result);
        assertEquals(Currency.JPY, result);
    }

    @Test
    void fromValueShouldThrowExceptionForInvalidValue() {
        String invalidCurrencyCode = "XYZ";
        RegistryDomainException exception = assertThrows(RegistryDomainException.class, () -> {
            Currency.fromValue(invalidCurrencyCode);
        });

        assertEquals(RegistryDomainErrorCode.INVALID_CURRENCY, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Unknown currency XYZ"));
    }

    @Test
    void fromValueShouldThrowNPEForNullValue() {
        String nullValue = null;
        assertThrows(RegistryDomainException.class, () -> {
            Currency.fromValue(nullValue);
        });
    }
}
