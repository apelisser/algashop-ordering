package com.apelisser.algashop.ordering.domain.model.validator;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.Objects;

public final class FieldValidations {

    private FieldValidations() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void requiresNonBlank(String value) {
        requiresNonBlank(value, null);
    }

    public static void requiresNonBlank(String value, String message) {
        Objects.requireNonNull(value, message);
        if (value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requiresValidEmail(String email) {
        requiresValidEmail(email, null);
    }

    public static void requiresValidEmail(String email, String message) {
        if (email == null || email.isBlank() || !EmailValidator.getInstance().isValid(email)) {
            throw new IllegalArgumentException(message);
        }
    }

}
