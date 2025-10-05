package com.apelisser.algashop.ordering.domain.entity;

import com.apelisser.algashop.ordering.domain.valueobject.Money;
import com.apelisser.algashop.ordering.domain.valueobject.Quantity;
import com.apelisser.algashop.ordering.domain.valueobject.id.CustomerId;
import com.apelisser.algashop.ordering.domain.valueobject.id.ShoppingCartId;

import java.time.OffsetDateTime;
import java.util.HashSet;

public class ShoppingCartTestDataBuilder {

    public static ShoppingCartId DEFAULT_SHOPPING_CART_ID = new ShoppingCartId();

    private CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;
    private OffsetDateTime createdAt = OffsetDateTime.now();
    private boolean withItems = true;

    private ShoppingCartTestDataBuilder() {}

    public static ShoppingCartTestDataBuilder aShoppingCart() {
        return new ShoppingCartTestDataBuilder();
    }

    public ShoppingCart build() {
        ShoppingCart shoppingCart = ShoppingCart.existing()
            .id(DEFAULT_SHOPPING_CART_ID)
            .customerId(customerId)
            .createdAt(createdAt)
            .totalAmount(Money.ZERO)
            .totalItems(Quantity.ZERO)
            .createdAt(createdAt)
            .items(new HashSet<>())
            .build();

        if (withItems) {
            shoppingCart.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(1));
            shoppingCart.addItem(ProductTestDataBuilder.aProductAltRamMemory().build(), new Quantity(2));
            shoppingCart.addItem(ProductTestDataBuilder.aProductAltMousePad().build(), new Quantity(3));
        }
        return shoppingCart;
    }

    public ShoppingCartTestDataBuilder customerId(CustomerId customerId) {
        this.customerId = customerId;
        return this;
    }

    public ShoppingCartTestDataBuilder createdAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public ShoppingCartTestDataBuilder withItems(boolean withItems) {
        this.withItems = withItems;
        return this;
    }

}
