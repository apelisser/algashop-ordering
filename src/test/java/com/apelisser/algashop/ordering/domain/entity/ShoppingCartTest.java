package com.apelisser.algashop.ordering.domain.entity;

import com.apelisser.algashop.ordering.domain.exception.ProductOutOfStockException;
import com.apelisser.algashop.ordering.domain.exception.ShoppingCartDoesNotContainItemException;
import com.apelisser.algashop.ordering.domain.valueobject.Money;
import com.apelisser.algashop.ordering.domain.valueobject.Product;
import com.apelisser.algashop.ordering.domain.valueobject.ProductName;
import com.apelisser.algashop.ordering.domain.valueobject.Quantity;
import com.apelisser.algashop.ordering.domain.valueobject.id.CustomerId;
import com.apelisser.algashop.ordering.domain.valueobject.id.ShoppingCartItemId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ShoppingCartTest {

    @Test
    void shouldGenerateANewShoppingCart() {
        ShoppingCart shoppingCart = ShoppingCart.startShopping(new CustomerId());

        Assertions.assertWith(shoppingCart,
            cart -> Assertions.assertThat(cart.totalAmount()).isEqualTo(Money.ZERO),
            cart -> Assertions.assertThat(cart.totalItems()).isEqualTo(Quantity.ZERO),
            cart -> Assertions.assertThat(cart.isEmpty()).isTrue(),
            cart -> Assertions.assertThat(cart.createdAt()).isNotNull()
        );
    }

    @Test
    void givenUnavailableProduct_whenAddToShoppingCart_shouldGenerateException() {
        ShoppingCart shoppingCart = ShoppingCart.startShopping(new CustomerId());
        Product unavailableProduct = ProductTestDataBuilder.aProductUnavailable().build();

        Assertions.assertThatExceptionOfType(ProductOutOfStockException.class)
            .isThrownBy(() -> shoppingCart.addItem(unavailableProduct, new Quantity(1)));
        Assertions.assertWith(shoppingCart,
            cart -> Assertions.assertThat(cart.totalAmount()).isEqualTo(Money.ZERO),
            cart -> Assertions.assertThat(cart.totalItems()).isEqualTo(Quantity.ZERO),
            cart -> Assertions.assertThat(cart.isEmpty()).isTrue(),
            cart -> Assertions.assertThat(cart.createdAt()).isNotNull()
        );
    }

    @Test
    void givenShoppingCart_whenAddingTheSameProductTwice_shouldBeAddedToTheExisting() {
        ShoppingCart shoppingCart = ShoppingCart.startShopping(new CustomerId());
        Product product = ProductTestDataBuilder.aProduct()
            .price(new Money("10"))
            .build();

        shoppingCart.addItem(product, new Quantity(1));
        shoppingCart.addItem(product, new Quantity(3));

        Assertions.assertWith(shoppingCart,
            cart -> Assertions.assertThat(cart.totalItems()).isEqualTo(new Quantity(4)),
            cart -> Assertions.assertThat(cart.totalAmount()).isEqualTo(new Money("40")),
            cart -> Assertions.assertThat(cart.items()).hasSize(1)
        );
    }

    @Test
    void givenShoppingCart_whenAddingDifferentProducts_shouldBeAddedSeparately() {
        ShoppingCart shoppingCart = ShoppingCart.startShopping(new CustomerId());
        Product product = ProductTestDataBuilder.aProduct()
            .price(new Money("10"))
            .build();

        Product anotherProduct = ProductTestDataBuilder.aProductAltMousePad()
            .price(new Money("11"))
            .build();

        shoppingCart.addItem(product, new Quantity(1));
        shoppingCart.addItem(anotherProduct, new Quantity(3));

        Assertions.assertWith(shoppingCart,
            cart -> Assertions.assertThat(cart.totalItems()).isEqualTo(new Quantity(4)),
            cart -> Assertions.assertThat(cart.totalAmount()).isEqualTo(new Money("43")),
            cart -> Assertions.assertThat(cart.items()).hasSize(2)
        );
    }

    @Test
    void givenExistingProductInCart_whenRefreshingItem_shouldUpdateTheExisting() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
            .withItems(false)
            .build();

        Product product = ProductTestDataBuilder.aProduct()
            .name(new ProductName("Notebook X11"))
            .price(new Money("10"))
            .build();

        Product newVersionProduct = ProductTestDataBuilder.aProduct()
            .name(new ProductName("Notebook Z"))
            .price(new Money("11"))
            .inStock(false)
            .build();

        shoppingCart.addItem(product, new Quantity(1));
        shoppingCart.refreshItem(newVersionProduct);
        ShoppingCartItem updatedItem = shoppingCart.items().iterator().next();

        Assertions.assertWith(updatedItem,
            item -> Assertions.assertThat(item.name()).isEqualTo(new ProductName("Notebook Z")),
            item -> Assertions.assertThat(item.price()).isEqualTo(new Money("11")),
            item -> Assertions.assertThat(item.isAvailable()).isFalse()
        );
    }

    @Test
    void givenNullProduct_whenRefreshItem_shouldGenerateException() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
            .withItems(true)
            .build();

        Assertions.assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> shoppingCart.refreshItem(null));
    }

    @Test
    void givenShoppingCartWithItems_whenChangingItemQuantity_shouldUpdateTheExisting() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
            .withItems(false)
            .build();

        Product product = ProductTestDataBuilder.aProduct()
            .price(new Money("10"))
            .build();

        shoppingCart.addItem(product, new Quantity(1));
        ShoppingCartItem itemToChangeQuantity = shoppingCart.items().iterator().next();

        shoppingCart.changeItemQuantity(itemToChangeQuantity.id(), new Quantity(3));
        ShoppingCartItem updatedItem = shoppingCart.items().iterator().next();

        Assertions.assertWith(updatedItem,
            item -> Assertions.assertThat(item.quantity()).isEqualTo(new Quantity(3)),
            item -> Assertions.assertThat(item.totalAmount()).isEqualTo(new Money("30"))
        );
    }

    @Test
    void givenShoppingCartWithoutUnavailableItem_whenCheckUnavailable_shouldReturnFalse() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
            .withItems(true)
            .build();
        Assertions.assertThat(shoppingCart.containsUnavailableItems()).isFalse();
    }

    @Test
    void givenShoppingCartWithUnavailableItem_whenCheckUnavailable_shouldReturnTrue() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
            .withItems(true)
            .build();

        Product product = ProductTestDataBuilder.aProduct()
            .inStock(false)
            .build();

        shoppingCart.refreshItem(product);

        Assertions.assertThat(shoppingCart.containsUnavailableItems()).isTrue();
    }

    @Test
    void givenShoppingCart_whenRemovingAnInexistentItem_shouldGenerateException() {
        ShoppingCart shoppingCart = ShoppingCart.startShopping(new CustomerId());

        Assertions.assertThatExceptionOfType(ShoppingCartDoesNotContainItemException.class)
            .isThrownBy(() -> shoppingCart.removeItem(new ShoppingCartItemId()));
    }

    @Test
    void givenShoppingCartWithItems_whenRemovingAnExistingItem_shouldBeRemoved() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
            .withItems(true)
            .build();

        ShoppingCartItem itemToRemove = shoppingCart.items().stream().iterator().next();
        shoppingCart.removeItem(itemToRemove.id());

        Assertions.assertThat(shoppingCart.items()).doesNotContain(itemToRemove);
    }

    @Test
    void givenShoppingCartWithItems_whenRemovingNonExistingItem_shouldGenerateException() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
            .withItems(true)
            .build();

        Assertions.assertThatExceptionOfType(ShoppingCartDoesNotContainItemException.class)
            .isThrownBy(() -> shoppingCart.removeItem(new ShoppingCartItemId()));
    }

    @Test
    void givenShoppingCartWithItems_whenRemovingAllItems_shouldBeEmpty() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
            .withItems(true)
            .build();

        Assertions.assertWith(shoppingCart,
            cart -> Assertions.assertThat(cart.totalItems()).isNotEqualTo(Quantity.ZERO),
            cart -> Assertions.assertThat(cart.totalAmount()).isNotEqualTo(Money.ZERO),
            cart -> Assertions.assertThat(cart.isEmpty()).isFalse()
        );

        shoppingCart.empty();

        Assertions.assertWith(shoppingCart,
            cart -> Assertions.assertThat(cart.totalItems()).isEqualTo(Quantity.ZERO),
            cart -> Assertions.assertThat(cart.totalAmount()).isEqualTo(Money.ZERO),
            cart -> Assertions.assertThat(cart.isEmpty()).isTrue()
        );
    }

    @Test
    void givenShoppingCartWithSameId_whenComparing_shouldBeEqual() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        ShoppingCart anotherShoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();

        Assertions.assertThat(shoppingCart).isEqualTo(anotherShoppingCart);
    }

}