package com.apelisser.algashop.ordering.application.order.query;

import com.apelisser.algashop.ordering.application.utility.PageFilter;
import com.apelisser.algashop.ordering.domain.model.customer.Customer;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.customer.Customers;
import com.apelisser.algashop.ordering.domain.model.order.Order;
import com.apelisser.algashop.ordering.domain.model.order.OrderStatus;
import com.apelisser.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.order.Orders;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class OrderQueryServiceIT {

    @Autowired
    OrderQueryService queryService;

    @Autowired
    Orders orders;

    @Autowired
    Customers customers;

    @Test
    void shouldFindOrderById() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        Order order = OrderTestDataBuilder.anOrder().customerId(customer.id()).build();
        orders.add(order);

        OrderDetailOutput output = queryService.findById(order.id().value().toString());

        Assertions.assertThat(output)
            .extracting(
                OrderDetailOutput::getId,
                OrderDetailOutput::getTotalAmount
            ).containsExactly(
                order.id().toString(),
                order.totalAmount().value()
            );
    }

    @Test
    void shouldFilterByPage() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.DRAFT).withItems(false).customerId(customer.id()).build());
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).customerId(customer.id()).build());
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).customerId(customer.id()).build());
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.READY).customerId(customer.id()).build());
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.CANCELED).customerId(customer.id()).build());

        Page<OrderSummaryOutput> page = queryService.filter(new PageFilter(3, 0));

        Assertions.assertThat(page.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(5);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(3);
    }

}