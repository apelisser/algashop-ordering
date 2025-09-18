package com.apelisser.algashop.ordering.domain.entity;

import com.apelisser.algashop.ordering.domain.valueobject.Money;
import com.apelisser.algashop.ordering.domain.valueobject.ProductName;
import com.apelisser.algashop.ordering.domain.valueobject.Quantity;
import com.apelisser.algashop.ordering.domain.valueobject.id.CustomerId;
import com.apelisser.algashop.ordering.domain.valueobject.id.ProductId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class OrderTest {

    @Test
    void shouldGenerate() {
        Assertions.assertThatCode(() -> Order.draft(new CustomerId()))
            .doesNotThrowAnyException();
    }

    @Test
    void shouldAddItem() {
        Order order = Order.draft(new CustomerId());

        ProductId productId = new ProductId();

        order.addItem(
            productId,
            new ProductName("Keyboard"),
            new Money("100"),
            new Quantity(1)
        );

        Assertions.assertThat(order.items()).hasSize(1);

        OrderItem item = order.items().iterator().next();
        Assertions.assertWith(item,
            i -> Assertions.assertThat(i.productId()).isNotNull(),
            i -> Assertions.assertThat(i.productName()).isEqualTo(new ProductName("Keyboard")),
            i -> Assertions.assertThat(i.productId()).isEqualTo(productId),
            i -> Assertions.assertThat(i.price()).isEqualTo(new Money("100")),
            i -> Assertions.assertThat(i.quantity()).isEqualTo(new Quantity(1))
        );
    }

}