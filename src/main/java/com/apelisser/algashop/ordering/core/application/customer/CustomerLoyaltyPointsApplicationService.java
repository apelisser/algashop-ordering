package com.apelisser.algashop.ordering.core.application.customer;

import com.apelisser.algashop.ordering.core.domain.model.customer.Customer;
import com.apelisser.algashop.ordering.core.domain.model.customer.CustomerId;
import com.apelisser.algashop.ordering.core.domain.model.customer.CustomerLoyaltyPointsService;
import com.apelisser.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;
import com.apelisser.algashop.ordering.core.domain.model.customer.Customers;
import com.apelisser.algashop.ordering.core.domain.model.order.Order;
import com.apelisser.algashop.ordering.core.domain.model.order.OrderId;
import com.apelisser.algashop.ordering.core.domain.model.order.OrderNotFoundException;
import com.apelisser.algashop.ordering.core.domain.model.order.Orders;
import com.apelisser.algashop.ordering.core.ports.in.customer.ForAddingLoyaltyPoints;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CustomerLoyaltyPointsApplicationService implements ForAddingLoyaltyPoints {

    private final Customers customers;
    private final Orders orders;
    private final CustomerLoyaltyPointsService customerLoyaltyPointsService;

    public CustomerLoyaltyPointsApplicationService(Customers customers, Orders orders,
            CustomerLoyaltyPointsService customerLoyaltyPointsService) {
        this.customers = customers;
        this.orders = orders;
        this.customerLoyaltyPointsService = customerLoyaltyPointsService;
    }

    @Transactional
    @Override
    public void addLoyaltyPoints(UUID customerId, String orderId) {
        Order order = orders.ofId(new OrderId(orderId)).orElseThrow(OrderNotFoundException::new);
        Customer customer = customers.ofId(new CustomerId(customerId)).orElseThrow(CustomerNotFoundException::new);
        customerLoyaltyPointsService.addPoints(customer, order);
        customers.add(customer);
    }

}
