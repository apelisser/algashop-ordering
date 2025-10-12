package com.apelisser.algashop.ordering.domain.model.repository;

import com.apelisser.algashop.ordering.domain.model.entity.Order;
import com.apelisser.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.apelisser.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceEntityAssembler;
import com.apelisser.algashop.ordering.infrastructure.persistence.disassembler.OrderPersistenceEntityDisassembler;
import com.apelisser.algashop.ordering.infrastructure.persistence.provider.OrdersPersistenceProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

@DataJpaTest
@Import({
    OrdersPersistenceProvider.class,
    OrderPersistenceEntityAssembler.class,
    OrderPersistenceEntityDisassembler.class
})
class OrdersIT {

    Orders orders;

    @Autowired
    public OrdersIT(Orders orders) {
        this.orders = orders;
    }

    @Test
    void shouldPersistAndFind() {
        Order originalOrder = OrderTestDataBuilder.anOrder().build();
        OrderId orderId = originalOrder.id();

        orders.add(originalOrder);
        Optional<Order> possibleOrder = orders.ofId(orderId);

        Assertions.assertThat(possibleOrder).isPresent();

        Order savedOrder = possibleOrder.get();

        Assertions.assertThat(savedOrder).satisfies(
            order -> Assertions.assertThat(order.id()).isEqualTo(originalOrder.id()),
            order -> Assertions.assertThat(order.customerId()).isEqualTo(originalOrder.customerId()),
            order -> Assertions.assertThat(order.totalAmount()).isEqualTo(originalOrder.totalAmount()),
            order -> Assertions.assertThat(order.totalItems()).isEqualTo(originalOrder.totalItems()),
            order -> Assertions.assertThat(order.placedAt()).isEqualTo(originalOrder.placedAt()),
            order -> Assertions.assertThat(order.paidAt()).isEqualTo(originalOrder.paidAt()),
            order -> Assertions.assertThat(order.canceledAt()).isEqualTo(originalOrder.canceledAt()),
            order -> Assertions.assertThat(order.readyAt()).isEqualTo(originalOrder.readyAt()),
            order -> Assertions.assertThat(order.status()).isEqualTo(originalOrder.status()),
            order -> Assertions.assertThat(order.paymentMethod()).isEqualTo(originalOrder.paymentMethod())
        );
    }

}