package com.apelisser.algashop.ordering.core.ports.in.order;

import org.springframework.transaction.annotation.Transactional;

public interface ForManagingOrders {

    @Transactional
    void cancel(Long orderId);

    @Transactional
    void markAsPaid(Long orderId);

    @Transactional
    void markAsReady(Long orderId);

}
