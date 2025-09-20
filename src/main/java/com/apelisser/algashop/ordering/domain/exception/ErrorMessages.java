package com.apelisser.algashop.ordering.domain.exception;

public final class ErrorMessages {

    public static final String VALIDATION_ERROR_EMAIL_IS_INVALID = "Email is invalid";
    public static final String VALIDATION_ERROR_BIRTHDATE_MUST_IN_PAST = "BirthDate must be a past date";
    public static final String VALIDATION_ERROR_FULL_NAME_IS_NULL = "FullName cannot be null";
    public static final String VALIDATION_ERROR_FIRST_NAME_IS_BLANK = "First name cannot be blank";
    public static final String VALIDATION_ERROR_LAST_NAME_IS_BLANK = "Last name cannot be blank";
    public static final String VALIDATION_ERROR_PHONE_IS_BLANK = "Phone number cannot be blank";
    public static final String VALIDATION_ERROR_DOCUMENT_IS_BLANK = "Document cannot be blank";

    public static final String ERROR_CUSTOMER_ARCHIVED = "Customer is archived it can't be changed";
    public static final String ERROR_ORDER_STATUS_CANNOT_BE_CHANGED = "Cannot change order %s status from %s to %s";
    public static final String ERROR_ORDER_DELIVERY_DATE_CANNOT_IN_THE_PAST = "Order %s expected delivery date cannot be in the past";

    private ErrorMessages() {
        throw new UnsupportedOperationException("Utility class");
    }

}
