package com.apelisser.algashop.ordering.domain.model.order;

import com.apelisser.algashop.ordering.domain.model.DomainException;

import java.io.Serial;

public class OrderNotBelongsToCustomerException extends DomainException {

    @Serial
    private static final long serialVersionUID = -7453012210004862591L;

}
