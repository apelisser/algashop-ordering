package com.apelisser.algashop.ordering.domain.valueobject;

import lombok.Builder;

import java.time.LocalDate;

@Builder(toBuilder = true)
public record Shipping(Money cost, LocalDate expectedDate, Recipient recipient, Address address) {

    public Shipping {
        if (cost == null) {
            throw new IllegalArgumentException("Cost is required");
        }

        if (expectedDate == null) {
            throw new IllegalArgumentException("Expected date is required");
        }

        if (recipient == null) {
            throw new IllegalArgumentException("Recipient is required");
        }

        if (address == null) {
            throw new IllegalArgumentException("Address is required");
        }
    }

}
