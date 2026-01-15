package com.apelisser.algashop.ordering.domain.model.commons;

import com.apelisser.algashop.ordering.domain.model.ErrorMessages;

import java.util.Objects;

public record Phone(String value) {

    public Phone {
        Objects.requireNonNull(value);

        if (value.isBlank()) {
            throw new IllegalArgumentException(ErrorMessages.VALIDATION_ERROR_PHONE_IS_BLANK);
        }
    }

    @Override
    public String toString() {
        return value;
    }

}
