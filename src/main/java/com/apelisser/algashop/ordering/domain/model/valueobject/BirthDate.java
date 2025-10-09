package com.apelisser.algashop.ordering.domain.model.valueobject;

import com.apelisser.algashop.ordering.domain.model.exception.ErrorMessages;

import java.time.LocalDate;
import java.util.Objects;

public record BirthDate(LocalDate value) {

    public BirthDate {
        Objects.requireNonNull(value);

        if (value.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(ErrorMessages.VALIDATION_ERROR_BIRTHDATE_MUST_IN_PAST);
        }

    }

    public int age() {
        return LocalDate.now().getYear() - value().getYear();
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
