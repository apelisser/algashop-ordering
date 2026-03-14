package com.apelisser.algashop.ordering.application.shoppingcart.query;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class ShoppingCartOutputTestDataBuilder {

    public static UUID VALID_SHOPPING_CART_ID = UUID.fromString("fc8cd6a0-57ce-4395-9a05-8ccacfd45211");

    public static ShoppingCartOutput.ShoppingCartOutputBuilder aShoppingCart() {
        return ShoppingCartOutput.builder()
            .id(VALID_SHOPPING_CART_ID)
            .customerId(UUID.randomUUID())
            .totalItems(3)
            .totalAmount(new BigDecimal(2500))
            .items(List.of(
                aShoppingCartItem().build(),
                anotherShoppingCartItem().build())
            );
    }

    public static ShoppingCartItemOutput.ShoppingCartItemOutputBuilder aShoppingCartItem() {
        return ShoppingCartItemOutput.builder()
            .id(UUID.randomUUID())
            .productId(UUID.randomUUID())
            .price(new BigDecimal(1000))
            .quantity(1)
            .totalAmount(new BigDecimal(1000))
            .available(true)
            .name("Notebook");
    }

    public static ShoppingCartItemOutput.ShoppingCartItemOutputBuilder anotherShoppingCartItem() {
        return ShoppingCartItemOutput.builder()
            .id(UUID.randomUUID())
            .productId(UUID.randomUUID())
            .price(new BigDecimal(750))
            .quantity(2)
            .totalAmount(new BigDecimal(1500))
            .available(true)
            .name("Desktop");
    }

}
