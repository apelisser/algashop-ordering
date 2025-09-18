package com.apelisser.algashop.ordering.domain.entity;

import com.apelisser.algashop.ordering.domain.valueobject.Money;
import com.apelisser.algashop.ordering.domain.valueobject.ProductName;
import com.apelisser.algashop.ordering.domain.valueobject.Quantity;
import com.apelisser.algashop.ordering.domain.valueobject.id.OrderId;
import com.apelisser.algashop.ordering.domain.valueobject.id.ProductId;
import org.junit.jupiter.api.Test;

class OrderItemTest {

    @Test
    void shouldGenerate() {
        OrderItem.brandNew()
            .productId(new ProductId())
            .quantity(new Quantity(1))
            .orderId(new OrderId())
            .productName(new ProductName("Keyboard"))
            .price(new Money("100"))
            .build();
    }

}