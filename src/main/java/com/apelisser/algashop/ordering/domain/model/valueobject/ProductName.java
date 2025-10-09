package com.apelisser.algashop.ordering.domain.model.valueobject;

public record ProductName(String value) {

    public ProductName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or blank");
        }
    }

    @Override
    public String toString() {
        return value;
    }

}
