package com.apelisser.algashop.ordering.application.order.query;

import com.apelisser.algashop.ordering.domain.model.customer.Customer;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.customer.Customers;
import com.apelisser.algashop.ordering.domain.model.order.Order;
import com.apelisser.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.order.Orders;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

}