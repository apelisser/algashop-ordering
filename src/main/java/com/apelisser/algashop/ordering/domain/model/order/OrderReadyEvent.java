package com.apelisser.algashop.ordering.domain.model.order;

import com.apelisser.algashop.ordering.domain.model.customer.CustomerId;

import java.time.OffsetDateTime;

public record OrderReadyEvent(
    OrderId orderId,
    CustomerId customerId,
    OffsetDateTime readyAt
) {

}
