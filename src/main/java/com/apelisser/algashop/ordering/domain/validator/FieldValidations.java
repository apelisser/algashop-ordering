package com.apelisser.algashop.ordering.domain.validator;

import org.apache.commons.validator.routines.EmailValidator;

public final class FieldValidations {

    private FieldValidations() {
        throw new UnsupportedOperationException("Utility class");
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
