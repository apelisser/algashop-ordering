package com.apelisser.algashop.ordering.domain.model.service;

import com.apelisser.algashop.ordering.domain.model.entity.Order;
import com.apelisser.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.entity.PaymentMethod;
import com.apelisser.algashop.ordering.domain.model.entity.ProductTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.entity.ShoppingCart;
import com.apelisser.algashop.ordering.domain.model.entity.ShoppingCartTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.exception.ShoppingCartCantProceedToCheckoutException;
import com.apelisser.algashop.ordering.domain.model.valueobject.Billing;
import com.apelisser.algashop.ordering.domain.model.valueobject.Money;
import com.apelisser.algashop.ordering.domain.model.valueobject.Product;
import com.apelisser.algashop.ordering.domain.model.valueobject.Quantity;
import com.apelisser.algashop.ordering.domain.model.valueobject.Shipping;
import com.apelisser.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.apelisser.algashop.ordering.domain.model.valueobject.id.ProductId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CheckoutServiceTest {

    CheckoutService checkoutService = new CheckoutService();

    @Test
    void givenAValidShoppingCart_whenCheckout_shouldCreateAnOrderAndEmptyShoppingCart() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
            .withItems(false)
            .build();
        shoppingCart.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(2));
        shoppingCart.addItem(ProductTestDataBuilder.aProductAltRamMemory().build(), new Quantity(1));

        CustomerId customerId = shoppingCart.customerId();
        Money shoppingCartTotalAmount = shoppingCart.totalAmount();
        Quantity shoppingCartTotalItems = shoppingCart.totalItems();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        Billing billing = OrderTestDataBuilder.aBilling();
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        Order order = checkoutService.checkout(shoppingCart, billing, shipping, paymentMethod);

        Money expectedOrderTotalAmount = shoppingCartTotalAmount.add(shipping.cost());

        Assertions.assertThat(order).isNotNull();
        Assertions.assertThat(order.isPlaced()).isTrue();
        Assertions.assertThat(order.customerId()).isEqualTo(customerId);
        Assertions.assertThat(order.paymentMethod()).isEqualTo(paymentMethod);
        Assertions.assertThat(order.totalItems()).isEqualTo(shoppingCartTotalItems);
        Assertions.assertThat(order.billing()).isEqualTo(billing);
        Assertions.assertThat(order.shipping()).isEqualTo(shipping);
        Assertions.assertThat(order.totalAmount()).isEqualTo(expectedOrderTotalAmount);

        Assertions.assertThat(shoppingCart.isEmpty()).isTrue();
        Assertions.assertThat(shoppingCart.totalItems()).isEqualTo(Quantity.ZERO);
        Assertions.assertThat(shoppingCart.totalAmount()).isEqualTo(Money.ZERO);
    }

    @Test
    void givenAShoppingCartWithUnavailableItem_whenCheckout_shouldThrowExceptionAndNotModifyIt() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
            .withItems(false)
            .build();

        ProductId productId = ProductTestDataBuilder.DEFAULT_PRODUCT_ID;

        shoppingCart.addItem(ProductTestDataBuilder.aProduct().id(productId).build(), new Quantity(2));
        shoppingCart.addItem(ProductTestDataBuilder.aProductAltRamMemory().build(), new Quantity(1));

        Quantity expectedTotalItems = shoppingCart.totalItems();
        Money expectedTotalAmount = shoppingCart.totalAmount();

        Product unavailableProduct = ProductTestDataBuilder.aProduct().id(productId).inStock(false).build();
        shoppingCart.refreshItem(unavailableProduct);


        Assertions.assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
            .isThrownBy(() -> checkoutService.checkout(shoppingCart,
                OrderTestDataBuilder.aBilling(),
                OrderTestDataBuilder.aShipping(),
                PaymentMethod.CREDIT_CARD)
            );

        Assertions.assertThat(shoppingCart.totalAmount()).isEqualTo(expectedTotalAmount);
        Assertions.assertThat(shoppingCart.totalItems()).isEqualTo(expectedTotalItems);
        Assertions.assertThat(shoppingCart.isEmpty()).isFalse();
    }

    @Test
    void givenAnEmptyShoppingCart_whenCheckout_shouldThrowException() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
            .withItems(false)
            .build();

        Assertions.assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
            .isThrownBy(() -> checkoutService.checkout(shoppingCart,
                OrderTestDataBuilder.aBilling(),
                OrderTestDataBuilder.aShipping(),
                PaymentMethod.CREDIT_CARD)
            );
    }

}