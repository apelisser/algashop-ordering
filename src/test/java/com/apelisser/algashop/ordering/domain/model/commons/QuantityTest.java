package com.apelisser.algashop.ordering.domain.model.commons;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class QuantityTest {

    @Test
    void shouldGenerate() {
        Assertions.assertThatCode(() -> new Quantity(10)).doesNotThrowAnyException();
    }

    @Test
    void shouldNotGenerate() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Quantity(null));

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Quantity(-1));
    }

    @Test
    void shouldAddValue() {
        Quantity quantity = new Quantity(10);
        Quantity expectedValue = new Quantity(18);

        Assertions.assertThat(quantity.add(new Quantity(8))).isEqualTo(expectedValue);
    }

    @Test
    void shouldCompareTo() {
        Quantity greater = new Quantity(18);
        Quantity less = new Quantity(10);

        Assertions.assertThat(greater).isGreaterThan(less);
        Assertions.assertThat(less).isLessThan(greater);
        Assertions.assertThat(greater).isEqualByComparingTo(greater);
    }

}
