package com.apelisser.algashop.ordering.domain.valueobject;

import com.apelisser.algashop.ordering.domain.exception.ErrorMessages;

import java.util.Objects;

public record Document(String value) {

    public Document {
        Objects.requireNonNull(value);

        if (value.isBlank()) {
            throw new IllegalArgumentException(ErrorMessages.VALIDATION_ERROR_DOCUMENT_IS_BLANK);
        }
    }

    @Override
    public String toString() {
        return value;
    }

}
