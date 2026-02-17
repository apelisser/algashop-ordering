package com.apelisser.algashop.ordering.application.shoppingcart.query;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ShoppingCartOutput {

    private UUID id;
    private UUID customerId;
    private Integer totalItems;
    private BigDecimal totalAmount;
    private List<ShoppingCartItemOutput> items = new ArrayList<>();

    public ShoppingCartOutput() {
    }

    public ShoppingCartOutput(UUID id, UUID customerId, Integer totalItems, BigDecimal totalAmount, List<ShoppingCartItemOutput> items) {
        this.id = id;
        this.customerId = customerId;
        this.totalItems = totalItems;
        this.totalAmount = totalAmount;
        this.items = items;
    }

}
