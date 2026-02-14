package com.apelisser.algashop.ordering.application.order.query;

import com.apelisser.algashop.ordering.domain.model.order.OrderId;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class OrderSummaryOutput {

    private String id;
    private CustomerMinimalOutput customer;
    private Integer totalItems;
    private BigDecimal totalAmount;
    private OffsetDateTime placedAt;
    private OffsetDateTime paidAt;
    private OffsetDateTime canceledAt;
    private OffsetDateTime readyAt;
    private String status;
    private String paymentMethod;

    public OrderSummaryOutput() {
    }

    public OrderSummaryOutput(String id, CustomerMinimalOutput customer, Integer totalItems, BigDecimal totalAmount,
            OffsetDateTime placedAt, OffsetDateTime paidAt, OffsetDateTime canceledAt, OffsetDateTime readyAt,
            String status, String paymentMethod) {
        this.id = id;
        this.customer = customer;
        this.totalItems = totalItems;
        this.totalAmount = totalAmount;
        this.placedAt = placedAt;
        this.paidAt = paidAt;
        this.canceledAt = canceledAt;
        this.readyAt = readyAt;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }

    public OrderSummaryOutput(Long id, CustomerMinimalOutput customer, Integer totalItems, BigDecimal totalAmount,
            OffsetDateTime placedAt, OffsetDateTime paidAt, OffsetDateTime canceledAt, OffsetDateTime readyAt,
            String status, String paymentMethod) {
        this(
            new OrderId(id).toString(),
            customer,
            totalItems,
            totalAmount,
            placedAt,
            paidAt,
            canceledAt,
            readyAt,
            status,
            paymentMethod
        );
    }

}
