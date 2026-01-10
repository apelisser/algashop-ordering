package com.apelisser.algashop.ordering.domain.model.exception;

import java.io.Serial;

public class DomainException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -851697478533426007L;

    public DomainException() {
    }

    public DomainException(Throwable cause) {
        super(cause);
    }

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }

}
