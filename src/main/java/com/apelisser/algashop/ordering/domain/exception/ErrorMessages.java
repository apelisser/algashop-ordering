package com.apelisser.algashop.ordering.domain.exception;

public final class ErrorMessages {

    public static final String VALIDATION_ERROR_EMAIL_IS_INVALID = "Email is invalid";
    public static final String VALIDATION_ERROR_BIRTHDATE_MUST_IN_PAST = "BirthDate must be a past date";
    public static final String VALIDATION_ERROR_FULL_NAME_IS_NULL = "FullName cannot be null";
    public static final String VALIDATION_ERROR_FULL_NAME_IS_BLANK = "FullName cannot be blank";

    public static final String ERROR_CUSTOMER_ARCHIVED = "Customer is archived it can't be changed";

    private ErrorMessages() {
        throw new UnsupportedOperationException("Utility class");
    }

}
