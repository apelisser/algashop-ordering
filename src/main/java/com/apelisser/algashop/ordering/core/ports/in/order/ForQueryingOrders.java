package com.apelisser.algashop.ordering.core.ports.in.order;

import org.springframework.data.domain.Page;

public interface ForQueryingOrders {

    OrderDetailOutput findById(String id);

    Page<OrderSummaryOutput> filter(OrderFilter filter);

}
