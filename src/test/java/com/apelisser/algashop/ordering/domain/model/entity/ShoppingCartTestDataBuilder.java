package com.apelisser.algashop.ordering.domain.model.entity;

import com.apelisser.algashop.ordering.domain.model.valueobject.Quantity;
import com.apelisser.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.apelisser.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;

public class ShoppingCartTestDataBuilder {

    public static ShoppingCartId DEFAULT_SHOPPING_CART_ID = new ShoppingCartId();

    private CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;
    private boolean withItems = true;

    private ShoppingCartTestDataBuilder() {}

    public static ShoppingCartTestDataBuilder aShoppingCart() {
        return new ShoppingCartTestDataBuilder();
    }

    public ShoppingCart build() {
        ShoppingCart shoppingCart = ShoppingCart.startShopping(customerId);

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

    public ShoppingCartTestDataBuilder withItems(boolean withItems) {
        this.withItems = withItems;
        return this;
    }

}
