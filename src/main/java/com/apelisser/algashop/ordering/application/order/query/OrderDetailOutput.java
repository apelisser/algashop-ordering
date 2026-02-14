package com.apelisser.algashop.ordering.application.order.query;

import com.apelisser.algashop.ordering.application.checkout.BillingData;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class OrderDetailOutput {

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
    private ShippingData shipping;
    private BillingData billing;

    private List<OrderItemDetailOutput> items = new ArrayList<>();

    public OrderDetailOutput() {
    }

    public OrderDetailOutput(String id, CustomerMinimalOutput customer, Integer totalItems, BigDecimal totalAmount,
            OffsetDateTime placedAt, OffsetDateTime paidAt, OffsetDateTime canceledAt, OffsetDateTime readyAt,
            String status, String paymentMethod, ShippingData shipping, BillingData billing,
            List<OrderItemDetailOutput> items) {
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
        this.shipping = shipping;
        this.billing = billing;
        this.items = items;
    }

}
