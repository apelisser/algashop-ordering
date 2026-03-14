package com.apelisser.algashop.ordering.application.shoppingcart.management;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @NotNull
    @Positive
    private Integer quantity;

    @NotNull
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
