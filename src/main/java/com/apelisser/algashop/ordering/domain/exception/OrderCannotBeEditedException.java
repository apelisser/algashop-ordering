package com.apelisser.algashop.ordering.domain.exception;

import com.apelisser.algashop.ordering.domain.entity.OrderStatus;
import com.apelisser.algashop.ordering.domain.valueobject.id.OrderId;

public class OrderCannotBeEditedException extends DomainException {

    public OrderCannotBeEditedException(OrderId id, OrderStatus status) {
        super(String.format(ErrorMessages.ERROR_ORDER_CANNOT_BE_EDITED, id, status));
    }

}
