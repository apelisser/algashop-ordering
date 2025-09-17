package com.apelisser.algashop.ordering.domain.valueobject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class MoneyTest {

    @Test
    void shouldGenerate() {
        Money expectedValue = new Money(BigDecimal.TEN);

        Assertions.assertThat(new Money(BigDecimal.TEN)).isEqualTo(expectedValue);
        Assertions.assertThat(new Money("10")).isEqualTo(expectedValue);
    }

    @Test
    void shouldNotGenerate() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Money((BigDecimal) null));

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Money("-1"));
    }

    @Test
    void shouldMutiplyValue() {
        Quantity multiplier = new Quantity(2);
        Money money = new Money(BigDecimal.TEN);
        Money expectedValue = new Money("20");

        Assertions.assertThat(money.multiply(multiplier)).isEqualTo(expectedValue);
    }

    @Test
    void shouldAddValue() {
        Money money = new Money(BigDecimal.TEN);
        Money expectedValue = new Money("18");

        Assertions.assertThat(money.add(new Money("8"))).isEqualTo(expectedValue);
    }

    @Test
    void shouldDivideValue() {
        Money money = new Money("15");
        Money expectedValue = new Money("7.5");

        Assertions.assertThat(money.divide(new Money("2"))).isEqualTo(expectedValue);
    }

    @Test
    void shouldCompareTo() {
        Money greater = new Money("18");
        Money less = new Money("10");

        Assertions.assertThat(greater).isGreaterThan(less);
        Assertions.assertThat(less).isLessThan(greater);
        Assertions.assertThat(greater).isEqualByComparingTo(greater);
    }

}
