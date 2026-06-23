package com.apelisser.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler;

import java.io.Serial;

public class BadGatewayException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -9127313986019015120L;

    public BadGatewayException() {
    }

    public BadGatewayException(String message, Throwable cause) {
        super(message, cause);
    }

}
