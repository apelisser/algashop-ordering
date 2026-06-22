package com.apelisser.algashop.ordering.core.application.order;

import com.apelisser.algashop.ordering.core.domain.model.order.Order;
import com.apelisser.algashop.ordering.core.domain.model.order.OrderId;
import com.apelisser.algashop.ordering.core.domain.model.order.OrderNotFoundException;
import com.apelisser.algashop.ordering.core.domain.model.order.Orders;
import com.apelisser.algashop.ordering.core.ports.in.order.ForManagingOrders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class OrderManagementApplicationService implements ForManagingOrders {

    private final Orders orders;

    public OrderManagementApplicationService(Orders orders) {
        this.orders = orders;
    }

    @Transactional
    @Override
    public void cancel(Long orderId) {
        Objects.requireNonNull(orderId);
        Order order = orders.ofId(new OrderId(orderId))
            .orElseThrow(OrderNotFoundException::new);
        order.cancel();
        orders.add(order);
    }

    @Transactional
    @Override
    public void markAsPaid(Long orderId) {
        Objects.requireNonNull(orderId);
        Order order = orders.ofId(new OrderId(orderId))
            .orElseThrow(OrderNotFoundException::new);
        order.markAsPaid();
        orders.add(order);
    }

    @Transactional
    @Override
    public void markAsReady(Long orderId) {
        Objects.requireNonNull(orderId);
        Order order = orders.ofId(new OrderId(orderId))
            .orElseThrow(OrderNotFoundException::new);
        order.markAsReady();
        orders.add(order);
    }

}
