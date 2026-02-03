package com.apelisser.algashop.ordering.application.checkout;

import com.apelisser.algashop.ordering.domain.model.commons.Money;
import com.apelisser.algashop.ordering.domain.model.commons.Quantity;
import com.apelisser.algashop.ordering.domain.model.customer.Customer;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.customer.Customers;
import com.apelisser.algashop.ordering.domain.model.order.Order;
import com.apelisser.algashop.ordering.domain.model.order.OrderId;
import com.apelisser.algashop.ordering.domain.model.order.Orders;
import com.apelisser.algashop.ordering.domain.model.order.shipping.ShippingCostService;
import com.apelisser.algashop.ordering.domain.model.product.Product;
import com.apelisser.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCartCantProceedToCheckoutException;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCartTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCarts;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@SpringBootTest
@Transactional
class CheckoutApplicationServiceIT {

    @Autowired
    CheckoutApplicationService checkoutApplicationService;


    @MockitoBean
    ShippingCostService shippingCostService;

    @Autowired
    Customers customers;

    @Autowired
    ShoppingCarts shoppingCarts;

    @Autowired
    Orders orders;

    @BeforeEach
    void setUp() {
        Mockito.when(shippingCostService.calculate(Mockito.any(ShippingCostService.CalculationRequest.class)))
            .thenReturn(new ShippingCostService.CalculationResult(
                new Money("20"),
                LocalDate.now().plusDays(5)));
    }

    @Test
    void shouldCheckout() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customers.add(customer);

        ShoppingCart newShoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
            .withItems(true)
            .customerId(customer.id())
            .build();
        shoppingCarts.add(newShoppingCart);

        CheckoutInput checkoutInput = CheckoutInputTestDataBuilder.aCheckoutInput()
            .shoppingCartId(newShoppingCart.id().value())
            .build();

        String orderId = checkoutApplicationService.checkout(checkoutInput);

        Order order = orders.ofId(new OrderId(orderId)).orElse(null);
        ShoppingCart shoppingCart = shoppingCarts.ofId(newShoppingCart.id()).orElse(null);

        Assertions.assertThat(order).isNotNull();
        Assertions.assertThat(shoppingCart).isNotNull();
        Assertions.assertThat(shoppingCart.totalItems()).isEqualTo(Quantity.ZERO);
        Assertions.assertThat(shoppingCart.totalAmount()).isEqualTo(Money.ZERO);
    }

    @Test
    void givenANonExistentShoppingCart_whenCheckout_shouldThrowException() {
        ShoppingCartId nonExistentShoppingCartId = new ShoppingCartId();

        CheckoutInput checkoutInput = CheckoutInputTestDataBuilder.aCheckoutInput()
            .shoppingCartId(nonExistentShoppingCartId.value())
            .build();

        Assertions.assertThatExceptionOfType(ShoppingCartNotFoundException.class)
            .isThrownBy(() -> checkoutApplicationService.checkout(checkoutInput));
    }

    @Test
    void givenAnEmptyShoppingCart_whenCheckout_shouldThrowException() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customers.add(customer);

        ShoppingCart newShoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
            .withItems(false)
            .customerId(customer.id())
            .build();
        shoppingCarts.add(newShoppingCart);

        CheckoutInput checkoutInput = CheckoutInputTestDataBuilder.aCheckoutInput()
            .shoppingCartId(newShoppingCart.id().value())
            .build();

        Assertions.assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
            .isThrownBy(() -> checkoutApplicationService.checkout(checkoutInput));
    }

    @Test
    void givenAShoppingCartWithOutOfStockItems_whenCheckout_shouldThrowException() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customers.add(customer);

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
            .withItems(true)
            .customerId(customer.id())
            .build();

        ShoppingCartItem shoppingCartItem = shoppingCart.items().stream().findFirst().orElseThrow();
        Product productOutOfStock = ProductTestDataBuilder.aProduct()
            .id(shoppingCartItem.productId())
            .inStock(false)
            .build();

        shoppingCart.refreshItem(productOutOfStock);

        shoppingCarts.add(shoppingCart);

        CheckoutInput checkoutInput = CheckoutInputTestDataBuilder.aCheckoutInput()
            .shoppingCartId(shoppingCart.id().value())
            .build();

        Assertions.assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
            .isThrownBy(() -> checkoutApplicationService.checkout(checkoutInput));
    }

}
