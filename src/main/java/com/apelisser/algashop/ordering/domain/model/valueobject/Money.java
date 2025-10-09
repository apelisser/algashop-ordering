package com.apelisser.algashop.ordering.domain.model.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Money(BigDecimal value) implements Comparable<Money> {

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public Money {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }

        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Value cannot be negative");
        }

        value = value.setScale(2, RoundingMode.HALF_EVEN);
    }

    public Money(String value) {
        this(new BigDecimal(value));
    }

    public Money multiply(Quantity quantity) {
        if (quantity.compareTo(new Quantity(1)) < 0) {
            throw new IllegalArgumentException("Quantity cannot be less than 1");
        }

        BigDecimal multiplier = new BigDecimal(quantity.value());
        return new Money(value.multiply(multiplier));
    }

    public Money add(Money other) {
        return new Money(value.add(other.value));
    }

    public Money divide(Money other) {
        return new Money(value.divide(other.value, RoundingMode.HALF_EVEN));
    }


    @Override
    public int compareTo(Money other) {
        return this.value.compareTo(other.value);
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
