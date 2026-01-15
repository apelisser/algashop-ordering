package com.apelisser.algashop.ordering.domain.model.commons;

import com.apelisser.algashop.ordering.domain.model.ErrorMessages;
import com.apelisser.algashop.ordering.domain.model.FieldValidations;

public record Email(String value) {

    public Email {
        FieldValidations.requiresValidEmail(value, ErrorMessages.VALIDATION_ERROR_EMAIL_IS_INVALID);
    }

    @Override
    public String toString() {
        return value;
    }

}
