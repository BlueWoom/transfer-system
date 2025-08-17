package com.domain.transfer.model;

import com.domain.transfer.exception.TransferDomainErrorCode;
import com.domain.transfer.exception.TransferDomainException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class CurrencyTest {

    @Test
    void fromValueShouldReturnCurrencyForValidUpperCaseValue() {
        String currencyCode = "USD";
        Currency result = Currency.fromValue(currencyCode);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(Currency.USD);
        assertThat(result.getValue()).isEqualTo("USD");
    }

    @Test
    void fromValueShouldReturnCurrencyForValidLowerCaseValue() {
        String currencyCode = "eur";
        Currency result = Currency.fromValue(currencyCode);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(Currency.EUR);
    }

    @Test
    void fromValueShouldReturnCurrencyForValidMixedCaseValue() {
        String currencyCode = "jPy";
        Currency result = Currency.fromValue(currencyCode);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(Currency.JPY);
    }

    @Test
    void fromValueShouldThrowExceptionForInvalidValue() {
        String invalidCurrencyCode = "XYZ";

        assertThatThrownBy(() -> Currency.fromValue(invalidCurrencyCode))
                .isInstanceOf(TransferDomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", TransferDomainErrorCode.INVALID_CURRENCY)
                .hasMessageContaining("Unknown currency XYZ");
    }

    @Test
    void fromValueShouldThrowNPEForNullValue() {
        String nullValue = null;

        assertThatThrownBy(() -> Currency.fromValue(nullValue))
                .isInstanceOf(TransferDomainException.class);
    }
}
