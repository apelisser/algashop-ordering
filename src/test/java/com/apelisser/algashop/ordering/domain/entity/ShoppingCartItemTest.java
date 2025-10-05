package com.apelisser.algashop.ordering.domain.entity;

import com.apelisser.algashop.ordering.domain.exception.ShoppingCartItemIncompatibleProductException;
import com.apelisser.algashop.ordering.domain.valueobject.Money;
import com.apelisser.algashop.ordering.domain.valueobject.Product;
import com.apelisser.algashop.ordering.domain.valueobject.ProductName;
import com.apelisser.algashop.ordering.domain.valueobject.Quantity;
import com.apelisser.algashop.ordering.domain.valueobject.id.ProductId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ShoppingCartItemTest {

    @Test
    void givenValidData_whenGeneratingShoppingCartItem_shouldGenerateCorrectly() {
        ShoppingCartItem shoppingCartItem = ShoppingCartItemTestDataBuilder.aShoppingCartItem()
            .shoppingCartId(ShoppingCartTestDataBuilder.DEFAULT_SHOPPING_CART_ID)
            .productId(ProductTestDataBuilder.DEFAULT_PRODUCT_ID)
            .productName(new ProductName("Mouse"))
            .price(new Money("70"))
            .quantity(new Quantity(3))
            .isAvailable(true)
            .build();

        Assertions.assertWith(shoppingCartItem,
            item -> Assertions.assertThat(item.shoppingCartId()).isEqualTo(ShoppingCartTestDataBuilder.DEFAULT_SHOPPING_CART_ID),
            item -> Assertions.assertThat(item.productId()).isEqualTo(ProductTestDataBuilder.DEFAULT_PRODUCT_ID),
            item -> Assertions.assertThat(item.name()).isEqualTo(new ProductName("Mouse")),
            item -> Assertions.assertThat(item.price()).isEqualTo(new Money("70")),
            item -> Assertions.assertThat(item.quantity()).isEqualTo(new Quantity(3)),
            item -> Assertions.assertThat(item.isAvailable()).isTrue(),
            item -> Assertions.assertThat(item.totalAmount()).isEqualTo(new Money("210"))
        );
    }

    @Test
    void givenValidQuantity_whenChangingQuantityOfShoppingCartItem_shouldChangeCorrectly() {
        ShoppingCartItem shoppingCartItem = ShoppingCartItemTestDataBuilder.aShoppingCartItem()
            .quantity(new Quantity(1))
            .price(new Money("70"))
            .build();

        Assertions.assertThatCode(() -> shoppingCartItem.changeQuantity(new Quantity(3)))
            .doesNotThrowAnyException();

        Assertions.assertWith(shoppingCartItem,
            item -> Assertions.assertThat(shoppingCartItem.quantity()).isEqualTo(new Quantity(3)),
            item -> Assertions.assertThat(shoppingCartItem.totalAmount()).isEqualTo(new Money("210"))
        );
    }

    @Test
    void givenACompatibleProduct_whenRefreshIsPerformed_shouldRefreshCorrectly() {
        ProductId productId = ProductTestDataBuilder.DEFAULT_PRODUCT_ID;

        ShoppingCartItem shoppingCartItem = ShoppingCartItemTestDataBuilder.aShoppingCartItem()
            .productId(productId)
            .price(new Money("70"))
            .quantity(new Quantity(2))
            .build();

        Product product = ProductTestDataBuilder.aProduct()
            .id(productId)
            .name(new ProductName("Headset"))
            .price(new Money("100"))
            .inStock(true)
            .build();

        Assertions.assertThatCode(() -> shoppingCartItem.refresh(product)).doesNotThrowAnyException();

        Assertions.assertWith(shoppingCartItem,
            item -> Assertions.assertThat(shoppingCartItem.productId()).isEqualTo(productId),
            item -> Assertions.assertThat(shoppingCartItem.name()).isEqualTo(new ProductName("Headset")),
            item -> Assertions.assertThat(shoppingCartItem.price()).isEqualTo(new Money("100")),
            item -> Assertions.assertThat(shoppingCartItem.isAvailable()).isTrue(),
            item -> Assertions.assertThat(shoppingCartItem.totalAmount()).isEqualTo(new Money("200"))
        );
    }

    @Test
    void givenAnIncompatibleProduct_whenRefreshIsPerformed_shouldThrowException() {
        ShoppingCartItem shoppingCartItem = ShoppingCartItemTestDataBuilder.aShoppingCartItem().build();

        ProductId anotherProductId = new ProductId();
        Product anotherProduct = ProductTestDataBuilder.aProduct()
            .id(anotherProductId)
            .build();

        Assertions.assertThatExceptionOfType(ShoppingCartItemIncompatibleProductException.class)
            .isThrownBy(() -> shoppingCartItem.refresh(anotherProduct));

        Assertions.assertThat(shoppingCartItem.productId()).isNotEqualTo(anotherProductId);
    }

    @Test
    void givenANullProduct_whenRefreshIsPerformed_shouldThrowException() {
        ShoppingCartItem shoppingCartItem = ShoppingCartItemTestDataBuilder.aShoppingCartItem().build();

        Assertions.assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> shoppingCartItem.refresh(null));
    }

    @Test
    void givenAnZeroQuantity_whenChangeQuantity_shouldThrowException() {
        ShoppingCartItem shoppingCartItem = ShoppingCartItemTestDataBuilder.aShoppingCartItem().build();

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> shoppingCartItem.changeQuantity(Quantity.ZERO));

        Assertions.assertThat(shoppingCartItem.quantity()).isNotEqualTo(Quantity.ZERO);
    }

    @Test
    void givenANullQuantity_whenChangeQuantity_shouldThrowException() {
        ShoppingCartItem shoppingCartItem = ShoppingCartItemTestDataBuilder.aShoppingCartItem()
            .quantity(new Quantity(18))
            .build();

        Assertions.assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> shoppingCartItem.changeQuantity(null));

        Assertions.assertThat(shoppingCartItem.quantity()).isEqualTo(new Quantity(18));
    }

}