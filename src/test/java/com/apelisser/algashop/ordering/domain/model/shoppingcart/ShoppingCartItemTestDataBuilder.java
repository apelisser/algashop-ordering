package com.apelisser.algashop.ordering.domain.model.shoppingcart;

import com.apelisser.algashop.ordering.domain.model.commons.Money;
import com.apelisser.algashop.ordering.domain.model.product.ProductName;
import com.apelisser.algashop.ordering.domain.model.commons.Quantity;
import com.apelisser.algashop.ordering.domain.model.product.ProductId;
import com.apelisser.algashop.ordering.domain.model.product.ProductTestDataBuilder;

public class ShoppingCartItemTestDataBuilder {

    private ShoppingCartId shoppingCartId = ShoppingCartTestDataBuilder.DEFAULT_SHOPPING_CART_ID;
    private ProductId productId = ProductTestDataBuilder.DEFAULT_PRODUCT_ID;
    private ProductName productName = new ProductName("Keyboard");
    private Money price = new Money("100");
    private Quantity quantity = new Quantity(3);
    private boolean available = true;

    public static ShoppingCartItemTestDataBuilder aShoppingCartItem() {
        return new ShoppingCartItemTestDataBuilder();
    }

    public ShoppingCartItem build() {
        return ShoppingCartItem.brandNew()
            .shoppingCartId(shoppingCartId)
            .productId(productId)
            .productName(productName)
            .price(price)
            .quantity(quantity)
            .available(available)
            .build();
    }

    public ShoppingCartItemTestDataBuilder shoppingCartId(ShoppingCartId shoppingCartId) {
        this.shoppingCartId = shoppingCartId;
        return this;
    }

    public ShoppingCartItemTestDataBuilder productId(ProductId productId) {
        this.productId = productId;
        return this;
    }

    public ShoppingCartItemTestDataBuilder productName(ProductName productName) {
        this.productName = productName;
        return this;
    }

    public ShoppingCartItemTestDataBuilder price(Money price) {
        this.price = price;
        return this;
    }

    public ShoppingCartItemTestDataBuilder quantity(Quantity quantity) {
        this.quantity = quantity;
        return this;
    }

    public ShoppingCartItemTestDataBuilder isAvailable(boolean available) {
        this.available = available;
        return this;
    }

}
