package com.apelisser.algashop.ordering.core.application.order;

import com.apelisser.algashop.ordering.core.ports.out.order.CustomerMinimalOutput;
import com.apelisser.algashop.ordering.core.ports.out.order.OrderSummaryOutput;
import com.apelisser.algashop.ordering.core.domain.model.customer.CustomerId;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class OrderSummaryOutputTestDataBuilder {

    public static OrderSummaryOutput.OrderSummaryOutputBuilder placedOrder() {
        return OrderSummaryOutput.builder()
            .id("123")
            .customer(CustomerMinimalOutput.builder()
                .id(new CustomerId().value())
                .firstName("John")
                .lastName("Doe")
                .document("12345")
                .email("johndoe@email.com")
                .phone("1191234564")
                .build())
            .totalItems(2)
            .totalAmount(new BigDecimal("39.98"))
            .placedAt(OffsetDateTime.now())
            .paidAt(null)
            .canceledAt(null)
            .readyAt(null)
            .status("PLACED")
            .paymentMethod("GATEWAY_BALANCE");
    }

}
