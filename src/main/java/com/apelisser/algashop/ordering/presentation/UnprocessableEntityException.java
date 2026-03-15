package com.apelisser.algashop.ordering.presentation;

import java.io.Serial;

public class UnprocessableEntityException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -3155370638356742072L;

    public UnprocessableEntityException() {
    }

    public UnprocessableEntityException(String message) {
        super(message);
    }

    public UnprocessableEntityException(String message, Throwable cause) {
        super(message, cause);
    }

}
