package com.apelisser.algashop.ordering.application.shoppingcart.query;

import com.apelisser.algashop.ordering.domain.model.commons.Money;
import com.apelisser.algashop.ordering.domain.model.commons.Quantity;
import com.apelisser.algashop.ordering.domain.model.customer.Customer;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerId;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.customer.Customers;
import com.apelisser.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCartTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCarts;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ShoppingCartQueryServiceIT {

    @Autowired
    ShoppingCartQueryService shoppingCartQueryService;

    @Autowired
    ShoppingCarts shoppingCarts;

    @Autowired
    Customers customers;

    @Test
    void shouldFindByShoppingCartId() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customers.add(customer);

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
            .customerId(customer.id())
            .withItems(false)
            .build();

        shoppingCart.addItem(ProductTestDataBuilder.aProduct().price(new Money("50")).build(), new Quantity(1));
        shoppingCart.addItem(ProductTestDataBuilder.aProductAltMousePad().price(new Money("100")).build(), new Quantity(1));
        shoppingCart.addItem(ProductTestDataBuilder.aProductAltRamMemory().price(new Money("1000")).build(), new Quantity(2));

        shoppingCarts.add(shoppingCart);

        ShoppingCartOutput shoppingCartOutput = shoppingCartQueryService.findById(shoppingCart.id().value());

        Assertions.assertThat(shoppingCartOutput).isNotNull();
        Assertions.assertThat(shoppingCartOutput.getId()).isEqualTo(shoppingCart.id().value());
        Assertions.assertThat(shoppingCartOutput.getCustomerId()).isEqualTo(shoppingCart.customerId().value());
        Assertions.assertThat(shoppingCartOutput.getItems()).hasSize(3);
        Assertions.assertThat(shoppingCartOutput.getTotalItems()).isEqualTo(4);
        Assertions.assertThat(shoppingCartOutput.getTotalAmount()).isEqualTo(new Money("2150").value());
    }

    @Test
    void shouldFindByCustomerId() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customers.add(customer);

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
            .customerId(customer.id())
            .withItems(false)
            .build();

        shoppingCart.addItem(ProductTestDataBuilder.aProduct().price(new Money("50")).build(), new Quantity(1));
        shoppingCart.addItem(ProductTestDataBuilder.aProductAltMousePad().price(new Money("100")).build(), new Quantity(1));
        shoppingCart.addItem(ProductTestDataBuilder.aProductAltRamMemory().price(new Money("1000")).build(), new Quantity(2));

        shoppingCarts.add(shoppingCart);

        ShoppingCartOutput shoppingCartOutput = shoppingCartQueryService.findByCustomerId(customer.id().value());

        Assertions.assertThat(shoppingCartOutput).isNotNull();
        Assertions.assertThat(shoppingCartOutput.getId()).isEqualTo(shoppingCart.id().value());
        Assertions.assertThat(shoppingCartOutput.getCustomerId()).isEqualTo(shoppingCart.customerId().value());
        Assertions.assertThat(shoppingCartOutput.getItems()).hasSize(3);
        Assertions.assertThat(shoppingCartOutput.getTotalItems()).isEqualTo(4);
        Assertions.assertThat(shoppingCartOutput.getTotalAmount()).isEqualTo(new Money("2150").value());
    }

    @Test
    void givenNonExistentShoppingCartId_whenFindByShoppingCartId_shouldThrowException() {
        ShoppingCartId nonExistentShoppingCartId = new ShoppingCartId();

        Assertions.assertThatExceptionOfType(ShoppingCartNotFoundException.class)
            .isThrownBy(() -> shoppingCartQueryService.findById(nonExistentShoppingCartId.value()));
    }

    @Test
    void givenNonExistentCustomerId_whenFindByShoppingCartId_shouldThrowException() {
        CustomerId nonExistentCustomerId = new CustomerId();

        Assertions.assertThatExceptionOfType(ShoppingCartNotFoundException.class)
            .isThrownBy(() -> shoppingCartQueryService.findByCustomerId(nonExistentCustomerId.value()));
    }

}