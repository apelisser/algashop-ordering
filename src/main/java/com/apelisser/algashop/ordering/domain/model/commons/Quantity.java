package com.apelisser.algashop.ordering.domain.model.commons;

public record Quantity(Integer value) implements Comparable<Quantity> {

    public static final Quantity ZERO = new Quantity(0);

    public Quantity {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }

        if (value < 0) {
            throw new IllegalArgumentException("Value cannot be negative");
        }
    }

    public Quantity add(Quantity other) {
        return new Quantity(this.value + other.value);
    }

    @Override
    public int compareTo(Quantity other) {
        return this.value.compareTo(other.value);
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
