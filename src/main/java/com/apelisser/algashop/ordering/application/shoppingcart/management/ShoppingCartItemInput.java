package com.apelisser.algashop.ordering.application.shoppingcart.management;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class ShoppingCartItemInput {

    private Integer quantity;
    private UUID productId;
    private UUID shoppingCartId;

    public ShoppingCartItemInput() {
    }

    public ShoppingCartItemInput(Integer quantity, UUID productId, UUID shoppingCartId) {
        this.quantity = quantity;
        this.productId = productId;
        this.shoppingCartId = shoppingCartId;
    }

}
