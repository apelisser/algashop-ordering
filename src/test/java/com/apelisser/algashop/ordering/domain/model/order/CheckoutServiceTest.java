package com.apelisser.algashop.ordering.domain.model.order;

import com.apelisser.algashop.ordering.domain.model.customer.Customer;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.apelisser.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCartTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCartCantProceedToCheckoutException;
import com.apelisser.algashop.ordering.domain.model.commons.Money;
import com.apelisser.algashop.ordering.domain.model.product.Product;
import com.apelisser.algashop.ordering.domain.model.commons.Quantity;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerId;
import com.apelisser.algashop.ordering.domain.model.product.ProductId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {

    CheckoutService checkoutService;

    @Mock
    Orders orders;

    @BeforeEach
    void setUp() {
        var specification = new CustomerHaveFreeShippingSpecification(
            orders,
            new LoyaltyPoints(100),
            2L,
            new LoyaltyPoints(2000)
        );
        checkoutService = new CheckoutService(specification);
    }

    @Test
    void givenAValidShoppingCart_whenCheckout_shouldCreateAnOrderAndEmptyShoppingCart() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
            .customerId(customer.id())
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

        Order order = checkoutService.checkout(customer, shoppingCart, billing, shipping, paymentMethod);

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
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
            .customerId(customer.id())
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
            .isThrownBy(() -> checkoutService.checkout(
                customer,
                shoppingCart,
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
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
            .customerId(customer.id())
            .withItems(false)
            .build();

        Assertions.assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
            .isThrownBy(() -> checkoutService.checkout(
                customer,
                shoppingCart,
                OrderTestDataBuilder.aBilling(),
                OrderTestDataBuilder.aShipping(),
                PaymentMethod.CREDIT_CARD)
            );
    }

    @Test
    void givenAValidShoppingCartAndCustomerWithFreeShipping_whenCheckout_shouldReturnPlacedOrderWithFreeShipping() {
        Customer customer = CustomerTestDataBuilder.existingCustomer()
            .loyaltyPoints(new LoyaltyPoints(3000))
            .build();

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
            .customerId(customer.id())
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

        Order order = checkoutService.checkout(customer, shoppingCart, billing, shipping, paymentMethod);

        Assertions.assertThat(order).isNotNull();
        Assertions.assertThat(order.isPlaced()).isTrue();
        Assertions.assertThat(order.customerId()).isEqualTo(customerId);
        Assertions.assertThat(order.paymentMethod()).isEqualTo(paymentMethod);
        Assertions.assertThat(order.totalItems()).isEqualTo(shoppingCartTotalItems);
        Assertions.assertThat(order.billing()).isEqualTo(billing);
        Assertions.assertThat(order.shipping()).isEqualTo(shipping.toBuilder().cost(Money.ZERO).build());
        Assertions.assertThat(order.totalAmount()).isEqualTo(shoppingCartTotalAmount);

        Assertions.assertThat(shoppingCart.isEmpty()).isTrue();
        Assertions.assertThat(shoppingCart.totalItems()).isEqualTo(Quantity.ZERO);
        Assertions.assertThat(shoppingCart.totalAmount()).isEqualTo(Money.ZERO);
    }

}