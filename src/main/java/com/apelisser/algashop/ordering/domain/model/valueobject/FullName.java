package com.apelisser.algashop.ordering.domain.model.valueobject;

import com.apelisser.algashop.ordering.domain.model.exception.ErrorMessages;

import java.util.Objects;

public record FullName(String firstName, String lastName) {

    public FullName(String firstName, String lastName) {
        Objects.requireNonNull(firstName);
        Objects.requireNonNull(lastName);

        if (firstName.isBlank()) {
            throw new IllegalArgumentException(ErrorMessages.VALIDATION_ERROR_FIRST_NAME_IS_BLANK);
        }

        if (lastName.isBlank()) {
            throw new IllegalArgumentException(ErrorMessages.VALIDATION_ERROR_LAST_NAME_IS_BLANK);
        }

        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

}
