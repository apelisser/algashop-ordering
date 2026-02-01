package com.apelisser.algashop.ordering.application.order.management;

import com.apelisser.algashop.ordering.domain.model.order.Order;
import com.apelisser.algashop.ordering.domain.model.order.OrderId;
import com.apelisser.algashop.ordering.domain.model.order.OrderNotFoundException;
import com.apelisser.algashop.ordering.domain.model.order.Orders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class OrderManagementApplicationService {

    private final Orders orders;

    public OrderManagementApplicationService(Orders orders) {
        this.orders = orders;
    }

    @Transactional
    public void cancel(Long orderId) {
        Objects.requireNonNull(orderId);
        Order order = orders.ofId(new OrderId(orderId))
            .orElseThrow(OrderNotFoundException::new);
        order.cancel();
        orders.add(order);
    }

    @Transactional
    public void markAsPaid(Long orderId) {
        Objects.requireNonNull(orderId);
        Order order = orders.ofId(new OrderId(orderId))
            .orElseThrow(OrderNotFoundException::new);
        order.markAsPaid();
        orders.add(order);
    }

    @Transactional
    public void markAsReady(Long orderId) {
        Objects.requireNonNull(orderId);
        Order order = orders.ofId(new OrderId(orderId))
            .orElseThrow(OrderNotFoundException::new);
        order.markAsReady();
        orders.add(order);
    }

}
