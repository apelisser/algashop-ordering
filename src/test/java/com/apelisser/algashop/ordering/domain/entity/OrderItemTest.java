package com.apelisser.algashop.ordering.domain.entity;

import com.apelisser.algashop.ordering.domain.valueobject.Quantity;
import com.apelisser.algashop.ordering.domain.valueobject.id.OrderId;
import org.junit.jupiter.api.Test;

class OrderItemTest {

    @Test
    void shouldGenerate() {
        OrderItem.brandNew()
            .product(ProductTestDataBuilder.aProduct().build())
            .quantity(new Quantity(1))
            .orderId(new OrderId())
            .build();
    }

}