package com.apelisser.algashop.ordering.domain.model.exception;

import java.io.Serial;

public class CustomerArchivedException extends DomainException {

    @Serial
    private static final long serialVersionUID = 3777829463935748988L;

    public CustomerArchivedException() {
        super(ErrorMessages.ERROR_CUSTOMER_ARCHIVED);
    }

    public CustomerArchivedException(Throwable cause) {
        super(ErrorMessages.ERROR_CUSTOMER_ARCHIVED, cause);
    }

}
