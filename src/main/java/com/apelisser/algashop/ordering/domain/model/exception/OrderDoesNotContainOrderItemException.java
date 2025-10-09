package com.apelisser.algashop.ordering.domain.model.exception;

import com.apelisser.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.apelisser.algashop.ordering.domain.model.valueobject.id.OrderItemId;

public class OrderDoesNotContainOrderItemException extends DomainException {

    public OrderDoesNotContainOrderItemException(OrderId id, OrderItemId orderItemId) {
        super(String.format(ErrorMessages.ERROR_ORDER_DOES_NOT_CONTAIN_ITEM, id, orderItemId));
    }

}
