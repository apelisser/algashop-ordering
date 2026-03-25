package com.apelisser.algashop.ordering.domain.model.customer;

import com.apelisser.algashop.ordering.domain.model.DomainException;
import com.apelisser.algashop.ordering.domain.model.ErrorMessages;

import java.io.Serial;

public class CustomerEmailIsInUseException extends DomainException {

    @Serial
    private static final long serialVersionUID = 8657558650929014106L;

    public CustomerEmailIsInUseException(CustomerId customerId) {
        super(ErrorMessages.ERROR_CUSTOMER_EMAIL_IS_IN_USE);
    }

}
