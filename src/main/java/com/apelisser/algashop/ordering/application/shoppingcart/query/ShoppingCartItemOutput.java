package com.apelisser.algashop.ordering.application.shoppingcart.query;

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
public class ShoppingCartItemOutput {

    private UUID id;
    private UUID productId;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalAmount;
    private Boolean available;

    public ShoppingCartItemOutput() {
    }

    public ShoppingCartItemOutput(UUID id, UUID productId, String name, BigDecimal price, Integer quantity,
            BigDecimal totalAmount, Boolean available) {
        this.id = id;
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.available = available;
    }

}
