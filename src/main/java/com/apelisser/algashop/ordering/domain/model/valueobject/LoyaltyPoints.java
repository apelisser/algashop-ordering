package com.apelisser.algashop.ordering.domain.model.valueobject;

import java.util.Objects;

public record LoyaltyPoints(Integer value) implements Comparable<LoyaltyPoints> {

    public static final LoyaltyPoints ZERO = new LoyaltyPoints(0);

    public LoyaltyPoints {
        Objects.requireNonNull(value);

        if (value < 0) {
            throw new IllegalArgumentException("Loyalty points cannot be negative");
        }
    }

    public LoyaltyPoints() {
        this(0);
    }

    public LoyaltyPoints add(LoyaltyPoints other) {
        Objects.requireNonNull(other);
        if (other.value() <= 0) {
            throw new IllegalArgumentException("Loyalty points cannot be negative or zero");
        }

        return new LoyaltyPoints(this.value() + other.value());
    }

    public LoyaltyPoints add(Integer value) {
        return add(new LoyaltyPoints(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public int compareTo(LoyaltyPoints o) {
        return this.value().compareTo(o.value());
    }

}
