package com.apelisser.algashop.ordering.domain.model;

import java.io.Serial;

public class DomainEntityNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -7236688341705477272L;

    public DomainEntityNotFoundException() {
    }

    public DomainEntityNotFoundException(String message) {
        super(message);
    }

}
