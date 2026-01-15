package com.apelisser.algashop.ordering.domain.model.shoppingcart;

import com.apelisser.algashop.ordering.domain.model.commons.Quantity;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerId;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.product.ProductTestDataBuilder;

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
