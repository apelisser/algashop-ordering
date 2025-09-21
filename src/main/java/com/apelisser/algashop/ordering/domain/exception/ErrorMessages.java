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
    public static final String ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_ITEMS = "Order %s cannot be placed has not items";
    public static final String ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_SHIPPING_INFO = "Order %s cannot be placed, it has no shipping info";
    public static final String ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_BILLING_INFO = "Order %s cannot be placed, it has no billing info";
    public static final String ERROR_ORDER_CANNOT_BE_PLACED_INVALID_SHIPPING_COST = "Order %s cannot be placed, it has no shipping cost";
    public static final String ERROR_ORDER_CANNOT_BE_PLACED_INVALID_EXPECTED_DELIVERY_DATE = "Order %s cannot be placed, it has no expected delivery date";
    public static final String ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_PAYMENT_METHOD = "Order %s cannot be placed, it has no payment method";
    public static final String ERROR_ORDER_DOES_NOT_CONTAIN_ITEM = "Order %s does not contain item %s";
    public static final String ERROR_PRODUCT_IS_OUT_OF_STOCK = "Product %s is out of stock";

    private ErrorMessages() {
        throw new UnsupportedOperationException("Utility class");
    }

}
