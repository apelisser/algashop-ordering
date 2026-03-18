package com.apelisser.algashop.ordering.presentation;

import java.io.Serial;

public class GatewayTimeoutException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -9127313986019015120L;

    public GatewayTimeoutException() {
    }

    public GatewayTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

}
