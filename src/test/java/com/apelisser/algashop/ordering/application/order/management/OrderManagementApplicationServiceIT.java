package com.apelisser.algashop.ordering.application.order.management;

import com.apelisser.algashop.ordering.application.customer.loyaltypoints.CustomerLoyaltyPointsApplicationService;
import com.apelisser.algashop.ordering.domain.model.customer.Customer;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.customer.Customers;
import com.apelisser.algashop.ordering.domain.model.order.*;
import com.apelisser.algashop.ordering.infrastructure.listener.order.OrderEventListener;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@SpringBootTest
@Transactional
class OrderManagementApplicationServiceIT {

    @Autowired
    OrderManagementApplicationService orderManagementApplicationService;

    @Autowired
    Orders orders;

    @Autowired
    Customers customers;

    @MockitoSpyBean
    OrderEventListener orderEventListener;

    @MockitoSpyBean
    CustomerLoyaltyPointsApplicationService customerLoyaltyPointsApplicationService;

    @Test
    void shouldCancelOrder() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customers.add(customer);

        Order order = OrderTestDataBuilder.anOrder()
            .customerId(customer.id())
            .status(OrderStatus.DRAFT)
            .build();
        orders.add(order);

        orderManagementApplicationService.cancel(order.id().value().toLong());

        Order canceledOrder = orders.ofId(order.id()).orElse(null);

        Assertions.assertThat(canceledOrder).isNotNull();
        Assertions.assertThat(canceledOrder.isCanceled()).isTrue();
        Assertions.assertThat(canceledOrder.canceledAt()).isNotNull();

        Mockito.verify(orderEventListener, Mockito.times(1))
            .listen(Mockito.any(OrderCanceledEvent.class));
    }

    @Test
    void givenANonExistentOrder_whenCancel_shouldThrowException() {
        OrderId nonExistentOrderId = new OrderId();
        Assertions.assertThatExceptionOfType(OrderNotFoundException.class)
            .isThrownBy(() -> orderManagementApplicationService.cancel(nonExistentOrderId.value().toLong()));
    }

    @Test
    void givenACanceledOrder_whenCancel_shouldThrowException() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customers.add(customer);

        Order order = OrderTestDataBuilder.anOrder()
            .customerId(customer.id())
            .status(OrderStatus.CANCELED)
            .build();
        orders.add(order);

        Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
            .isThrownBy(() -> orderManagementApplicationService.cancel(order.id().value().toLong()));
    }

    @Test
    void shouldMarkAsPaid() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customers.add(customer);

        Order order = OrderTestDataBuilder.anOrder()
            .customerId(customer.id())
            .status(OrderStatus.PLACED)
            .build();
        orders.add(order);

        orderManagementApplicationService.markAsPaid(order.id().value().toLong());

        Order paidOrder = orders.ofId(order.id()).orElse(null);

        Assertions.assertThat(paidOrder).isNotNull();
        Assertions.assertThat(paidOrder.isPaid()).isTrue();
        Assertions.assertThat(paidOrder.paidAt()).isNotNull();

        Mockito.verify(orderEventListener, Mockito.times(1))
            .listen(Mockito.any(OrderPaidEvent.class));
    }

    @Test
    void givenANonExistentOrder_whenMarkAsPaid_shouldThrowException() {
        OrderId nonExistentOrderId = new OrderId();
        Assertions.assertThatExceptionOfType(OrderNotFoundException.class)
            .isThrownBy(() -> orderManagementApplicationService.markAsPaid(nonExistentOrderId.value().toLong()));
    }

    @Test
    void givenADifferentOrderThanPlaced_whenMarkAsPaid_shouldThrowException() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customers.add(customer);

        Order order = OrderTestDataBuilder.anOrder()
            .customerId(customer.id())
            .status(OrderStatus.DRAFT)
            .build();
        orders.add(order);

        Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
            .isThrownBy(() -> orderManagementApplicationService.markAsPaid(order.id().value().toLong()));
    }

    @Test
    void shouldMarkAsReady() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customers.add(customer);

        Order order = OrderTestDataBuilder.anOrder()
            .customerId(customer.id())
            .status(OrderStatus.PAID)
            .build();
        orders.add(order);

        orderManagementApplicationService.markAsReady(order.id().value().toLong());

        Order readyOrder = orders.ofId(order.id()).orElse(null);

        Assertions.assertThat(readyOrder).isNotNull();
        Assertions.assertThat(readyOrder.isReady()).isTrue();
        Assertions.assertThat(readyOrder.readyAt()).isNotNull();

        Mockito.verify(orderEventListener, Mockito.times(1))
            .listen(Mockito.any(OrderReadyEvent.class));

        Mockito.verify(customerLoyaltyPointsApplicationService)
            .addLoyaltyPoints(
                Mockito.any(UUID.class),
                Mockito.any(String.class)
            );
    }

    @Test
    void givenANonExistentOrder_whenMarkAsReady_shouldThrowException() {
        OrderId nonExistentOrderId = new OrderId();
        Assertions.assertThatExceptionOfType(OrderNotFoundException.class)
            .isThrownBy(() -> orderManagementApplicationService.markAsReady(nonExistentOrderId.value().toLong()));
    }

    @Test
    void givenADifferentOrderThanPaid_whenMarkAsPaid_shouldThrowException() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customers.add(customer);

        Order order = OrderTestDataBuilder.anOrder()
            .customerId(customer.id())
            .status(OrderStatus.DRAFT)
            .build();
        orders.add(order);

        Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
            .isThrownBy(() -> orderManagementApplicationService.markAsReady(order.id().value().toLong()));
    }

}
