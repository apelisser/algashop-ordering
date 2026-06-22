package com.apelisser.algashop.ordering.core.application.order;

import com.apelisser.algashop.ordering.core.ports.in.order.OrderDetailOutput;
import com.apelisser.algashop.ordering.core.ports.in.order.OrderFilter;
import com.apelisser.algashop.ordering.core.ports.in.order.OrderSummaryOutput;
import com.apelisser.algashop.ordering.core.ports.in.order.ForQueryingOrders;
import com.apelisser.algashop.ordering.core.ports.out.order.ForObtainingOrders;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class OrderQueryService implements ForQueryingOrders {

    private final ForObtainingOrders forObtainingOrders;

    public OrderQueryService(ForObtainingOrders forObtainingOrders) {
        this.forObtainingOrders = forObtainingOrders;
    }

    @Override
    public OrderDetailOutput findById(String id) {
        return forObtainingOrders.findById(id);
    }

    @Override
    public Page<OrderSummaryOutput> filter(OrderFilter filter) {
        return forObtainingOrders.filter(filter);
    }

}
