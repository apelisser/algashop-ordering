package com.apelisser.algashop.ordering.application.order.query;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class OrderItemDetailOutput {

    private String id;
    private String orderId;
    private UUID productId;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalAmount;

    public OrderItemDetailOutput() {
    }

    public OrderItemDetailOutput(String id, String orderId, UUID productId, String name, BigDecimal price,
            Integer quantity, BigDecimal totalAmount) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
    }

}
