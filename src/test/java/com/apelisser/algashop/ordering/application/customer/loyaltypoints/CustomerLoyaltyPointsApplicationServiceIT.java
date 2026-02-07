package com.apelisser.algashop.ordering.application.customer.loyaltypoints;

import com.apelisser.algashop.ordering.domain.model.commons.Money;
import com.apelisser.algashop.ordering.domain.model.commons.Quantity;
import com.apelisser.algashop.ordering.domain.model.customer.CantAddLoyaltyPointsOrderIsNotReadyException;
import com.apelisser.algashop.ordering.domain.model.customer.Customer;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerArchivedException;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerId;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.customer.Customers;
import com.apelisser.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.apelisser.algashop.ordering.domain.model.order.Order;
import com.apelisser.algashop.ordering.domain.model.order.OrderId;
import com.apelisser.algashop.ordering.domain.model.order.OrderNotBelongsToCustomerException;
import com.apelisser.algashop.ordering.domain.model.order.OrderNotFoundException;
import com.apelisser.algashop.ordering.domain.model.order.OrderStatus;
import com.apelisser.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.order.Orders;
import com.apelisser.algashop.ordering.domain.model.product.Product;
import com.apelisser.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.apelisser.algashop.ordering.infrastructure.listener.customer.CustomerEventListener;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CustomerLoyaltyPointsApplicationServiceIT {

    @Autowired
    Customers customers;

    @Autowired
    Orders orders;

    @Autowired
    CustomerLoyaltyPointsApplicationService customerLoyaltyPointsApplicationService;

    @MockitoBean
    CustomerEventListener customerEventListener;

    @Test
    void shouldAddLoyaltyPoints() {
        Customer customer = CustomerTestDataBuilder.existingCustomer()
            .id(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)
            .loyaltyPoints(LoyaltyPoints.ZERO)
            .build();

        customers.add(customer);

        Order order = OrderTestDataBuilder.anOrder()
            .customerId(customer.id())
            .status(OrderStatus.DRAFT)
            .withItems(false)
            .build();

        Product product = ProductTestDataBuilder.aProduct()
            .price(new Money("2000"))
            .build();

        order.addItem(product, new Quantity(2));
        order.place();
        order.markAsPaid();
        order.markAsReady();

        orders.add(order);

        customerLoyaltyPointsApplicationService.addLoyaltyPoints(customer.id().value(), order.id().toString());

        Customer updatedCustomer = customers.ofId(customer.id()).orElse(null);

        Assertions.assertThat(updatedCustomer).isNotNull();
        Assertions.assertThat(updatedCustomer.loyaltyPoints()).isEqualTo(new LoyaltyPoints(20));
    }

    @Test
    void shouldNotAddLoyaltyPoints() {
        Customer customer = CustomerTestDataBuilder.existingCustomer()
            .id(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)
            .loyaltyPoints(LoyaltyPoints.ZERO)
            .build();

        customers.add(customer);

        Order order = OrderTestDataBuilder.anOrder()
            .customerId(customer.id())
            .status(OrderStatus.DRAFT)
            .withItems(false)
            .build();

        Product product = ProductTestDataBuilder.aProduct()
            .price(new Money("300"))
            .build();

        order.addItem(product, new Quantity(2));
        order.place();
        order.markAsPaid();
        order.markAsReady();

        orders.add(order);

        customerLoyaltyPointsApplicationService.addLoyaltyPoints(customer.id().value(), order.id().toString());

        Customer updatedCustomer = customers.ofId(customer.id()).orElse(null);

        Assertions.assertThat(updatedCustomer).isNotNull();
        Assertions.assertThat(updatedCustomer.loyaltyPoints()).isEqualTo(LoyaltyPoints.ZERO);
    }

    @Test
    void givenANonExistentCustomer_whenAddLoyaltyPoints_shouldThrowException() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        Order order = OrderTestDataBuilder.anOrder()
            .customerId(customer.id())
            .withItems(true)
            .status(OrderStatus.READY)
            .build();
        orders.add(order);

        Assertions.assertThatExceptionOfType(CustomerNotFoundException.class)
            .isThrownBy(() -> customerLoyaltyPointsApplicationService.addLoyaltyPoints(
                new CustomerId().value(),
                order.id().toString()));
    }

    @Test
    void givenANonExistentOrder_whenAddLoyaltyPoints_shouldThrowException() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        Order order = OrderTestDataBuilder.anOrder()
            .customerId(customer.id())
            .withItems(true)
            .status(OrderStatus.READY)
            .build();
        orders.add(order);

        Assertions.assertThatExceptionOfType(OrderNotFoundException.class)
            .isThrownBy(() -> customerLoyaltyPointsApplicationService.addLoyaltyPoints(
                customer.id().value(),
                new OrderId().toString()));
    }

    @Test
    void givenAnArchivedCustomer_whenAddLoyaltyPoints_shouldThrowException() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        Order order = OrderTestDataBuilder.anOrder()
            .customerId(customer.id())
            .withItems(true)
            .status(OrderStatus.READY)
            .build();
        orders.add(order);

        customer.archive();
        customers.add(customer);

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
            .isThrownBy(() -> customerLoyaltyPointsApplicationService.addLoyaltyPoints(
                customer.id().value(),
                order.id().toString()));
    }

    @Test
    void givenAnOrderFromAnotherCustomer_whenAddLoyaltyPoints_shouldThrowException() {
        Customer customer1 = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer1);

        Order order1 = OrderTestDataBuilder.anOrder()
            .customerId(customer1.id())
            .withItems(true)
            .status(OrderStatus.READY)
            .build();
        orders.add(order1);

        Customer customer2 = CustomerTestDataBuilder.existingCustomer()
            .id(new CustomerId())
            .build();
        customers.add(customer2);

        Order order2 = OrderTestDataBuilder.anOrder()
            .customerId(customer2.id())
            .withItems(true)
            .status(OrderStatus.READY)
            .build();
        orders.add(order2);

        Assertions.assertThatExceptionOfType(OrderNotBelongsToCustomerException.class)
            .isThrownBy(() -> customerLoyaltyPointsApplicationService.addLoyaltyPoints(
                customer1.id().value(),
                order2.id().toString()));
    }

    @Test
    void givenAnOrderWithAStatusOtherThanReady_whenAddLoyaltyPoints_shouldThrowException() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        Order order = OrderTestDataBuilder.anOrder()
            .customerId(customer.id())
            .withItems(true)
            .status(OrderStatus.PLACED)
            .build();
        orders.add(order);

        Assertions.assertThatExceptionOfType(CantAddLoyaltyPointsOrderIsNotReadyException.class)
            .isThrownBy(() -> customerLoyaltyPointsApplicationService.addLoyaltyPoints(
                customer.id().value(),
                order.id().toString()));
    }

}