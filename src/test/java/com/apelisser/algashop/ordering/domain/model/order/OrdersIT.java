package com.apelisser.algashop.ordering.domain.model.order;

import com.apelisser.algashop.ordering.domain.model.customer.Customers;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.commons.Money;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerId;
import com.apelisser.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityAssembler;
import com.apelisser.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntityAssembler;
import com.apelisser.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityDisassembler;
import com.apelisser.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntityDisassembler;
import com.apelisser.algashop.ordering.infrastructure.persistence.customer.CustomersPersistenceProvider;
import com.apelisser.algashop.ordering.infrastructure.persistence.order.OrdersPersistenceProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.time.Year;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@Import({
    OrdersPersistenceProvider.class,
    OrderPersistenceEntityAssembler.class,
    OrderPersistenceEntityDisassembler.class,
    CustomersPersistenceProvider.class,
    CustomerPersistenceEntityAssembler.class,
    CustomerPersistenceEntityDisassembler.class
})
class OrdersIT {

    Orders orders;
    Customers customers;

    @Autowired
    public OrdersIT(Orders orders,  Customers customers) {
        this.orders = orders;
        this.customers = customers;
    }

    @BeforeEach
    void setUp() {
        if (!customers.exists(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)) {
            customers.add(CustomerTestDataBuilder.existingCustomer().build());
        }
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

    @Test
    void shouldUpdateExistingOrder() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        orders.add(order);

        order = orders.ofId(order.id()).orElseThrow();

        order.markAsPaid();
        orders.add(order);

        order = orders.ofId(order.id()).orElseThrow();

        Assertions.assertThat(order.isPaid()).isTrue();
    }

    @Test
    void shouldNotAllowStaleUpdates() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        orders.add(order);

        Order orderT1 = orders.ofId(order.id()).orElseThrow();
        Order orderT2 = orders.ofId(order.id()).orElseThrow();

        orderT1.markAsPaid();
        orders.add(orderT1);

        orderT2.cancel();

        Assertions.assertThatExceptionOfType(ObjectOptimisticLockingFailureException.class)
            .isThrownBy(() -> orders.add(orderT2));

        Order savedOrder = orders.ofId(order.id()).orElseThrow();

        Assertions.assertThat(savedOrder.paidAt()).isNotNull();
        Assertions.assertThat(savedOrder.canceledAt()).isNull();
    }

    @Test
    public void shouldCountExistingOrders() {
        Assertions.assertThat(orders.count()).isZero();

        Order order1 = OrderTestDataBuilder.anOrder().build();
        Order order2 = OrderTestDataBuilder.anOrder().build();
        orders.add(order1);
        orders.add(order2);

        Assertions.assertThat(orders.count()).isEqualTo(2);
    }

    @Test
    void shouldReturnIfOrderExists() {
        Order order = OrderTestDataBuilder.anOrder().build();
        orders.add(order);

        Assertions.assertThat(orders.exists(order.id())).isTrue();
        Assertions.assertThat(orders.exists(new OrderId())).isFalse();
    }

    @Test
    void shouldListExistingOrdersByYear() {
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build());
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build());
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.CANCELED).build());
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.DRAFT).build());

        CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;

        List<Order> listedOrders1 = this.orders.placedByCustomerInYear(customerId, Year.now());
        Assertions.assertThat(listedOrders1).isNotEmpty();
        Assertions.assertThat(listedOrders1.size()).isEqualTo(2);

        List<Order> listedOrders2 =  this.orders.placedByCustomerInYear(customerId, Year.now().minusYears(1));
        Assertions.assertThat(listedOrders2).isEmpty();
    }

    @Test
    void shouldReturnTotalSoldByCustomer() {
        Order order1 = OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).build();
        Order order2 = OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).build();
        orders.add(order1);
        orders.add(order2);

        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.CANCELED).build());
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build());


        Money expectedTotalAmount = order1.totalAmount().add(order2.totalAmount());

        CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;

        Assertions.assertThat(orders.totalSoldForCustomer(customerId)).isEqualTo(expectedTotalAmount);
        Assertions.assertThat(orders.totalSoldForCustomer(new CustomerId())).isEqualTo(Money.ZERO);
    }

    @Test
    void shouldReturnSalesQuantityByCustomer() {
        Order order1 = OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).build();
        Order order2 = OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).build();
        orders.add(order1);
        orders.add(order2);

        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.CANCELED).build());
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build());

        CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;

        Assertions.assertThat(orders.salesQuantityByCustomerInYear(customerId, Year.now())).isEqualTo(2L);
        Assertions.assertThat(orders.salesQuantityByCustomerInYear(customerId, Year.now().minusYears(1))).isZero();
    }

}