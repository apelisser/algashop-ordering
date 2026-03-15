package com.apelisser.algashop.ordering.domain.model.product;

import com.apelisser.algashop.ordering.domain.model.DomainEntityNotFoundException;
import com.apelisser.algashop.ordering.domain.model.ErrorMessages;

import java.io.Serial;

public class ProductNotFoundException extends DomainEntityNotFoundException {

    @Serial
    private static final long serialVersionUID = 5850587762258766081L;

    public ProductNotFoundException() {
    }

    public ProductNotFoundException(ProductId productId) {
        super(String.format(ErrorMessages.ERROR_PRODUCT_NOT_FOUND, productId));
    }

}
