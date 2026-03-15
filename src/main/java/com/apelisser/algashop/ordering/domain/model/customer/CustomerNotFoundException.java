package com.apelisser.algashop.ordering.domain.model.customer;

import com.apelisser.algashop.ordering.domain.model.DomainEntityNotFoundException;
import com.apelisser.algashop.ordering.domain.model.ErrorMessages;

import java.io.Serial;

public class CustomerNotFoundException extends DomainEntityNotFoundException {

    @Serial
    private static final long serialVersionUID = -7737167013540066408L;

    public CustomerNotFoundException() {
    }

    public CustomerNotFoundException(CustomerId customerId) {
        super(String.format(ErrorMessages.ERROR_CUSTOMER_NOT_FOUND, customerId));
    }

}
