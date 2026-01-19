package com.apelisser.algashop.ordering.domain.model.customer;

import com.apelisser.algashop.ordering.domain.model.order.Order;
import com.apelisser.algashop.ordering.domain.model.order.OrderStatus;
import com.apelisser.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.product.Product;
import com.apelisser.algashop.ordering.domain.model.commons.Quantity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CustomerLoyaltyPointsServiceTest {

    CustomerLoyaltyPointsService customerLoyaltyPointsService = new CustomerLoyaltyPointsService();

    @Test
    void givenValidCustomerAndOrder_whenAddingPoints_shouldAccumulate() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.READY).build();

        customerLoyaltyPointsService.addPoints(customer, order);

        Assertions.assertThat(customer.loyaltyPoints()).isEqualTo(new LoyaltyPoints(40));
    }

    @Test
    void givenValidCustomerAndOrderWithLowTotalAmount_whenAddingPoints_shouldNotAccumulate() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        Product product = ProductTestDataBuilder.aProductAltMousePad().build();

        Order order = OrderTestDataBuilder.anOrder()
            .withItems(false)
            .status(OrderStatus.DRAFT).build();

        order.addItem(product, new Quantity(1));
        order.place();
        order.markAsPaid();
        order.markAsReady();

        customerLoyaltyPointsService.addPoints(customer, order);

        Assertions.assertThat(customer.loyaltyPoints()).isEqualTo(new LoyaltyPoints(10));
    }

}