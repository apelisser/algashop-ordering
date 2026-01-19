package com.apelisser.algashop.ordering.application.customer.loyaltypoints;

import com.apelisser.algashop.ordering.domain.model.customer.Customer;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerId;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerLoyaltyPointsService;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.apelisser.algashop.ordering.domain.model.customer.Customers;
import com.apelisser.algashop.ordering.domain.model.order.Order;
import com.apelisser.algashop.ordering.domain.model.order.OrderId;
import com.apelisser.algashop.ordering.domain.model.order.OrderNotFoundException;
import com.apelisser.algashop.ordering.domain.model.order.Orders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CustomerLoyaltyPointsApplicationService {

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
    public void addLoyaltyPoints(UUID customerId, String orderId) {
        Order order = orders.ofId(new OrderId(orderId)).orElseThrow(OrderNotFoundException::new);
        Customer customer = customers.ofId(new CustomerId(customerId)).orElseThrow(CustomerNotFoundException::new);
        customerLoyaltyPointsService.addPoints(customer, order);
        customers.add(customer);
    }

}
