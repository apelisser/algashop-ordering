package com.apelisser.algashop.ordering.application.shoppingcart.management;

import com.apelisser.algashop.ordering.domain.model.commons.Money;
import com.apelisser.algashop.ordering.domain.model.commons.Quantity;
import com.apelisser.algashop.ordering.domain.model.customer.Customer;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerAlreadyHaveShoppingCartException;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerId;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.customer.Customers;
import com.apelisser.algashop.ordering.domain.model.product.Product;
import com.apelisser.algashop.ordering.domain.model.product.ProductCatalogService;
import com.apelisser.algashop.ordering.domain.model.product.ProductId;
import com.apelisser.algashop.ordering.domain.model.product.ProductNotFoundException;
import com.apelisser.algashop.ordering.domain.model.product.ProductOutOfStockException;
import com.apelisser.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCartDoesNotContainItemException;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCartItemId;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCartTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCarts;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@Transactional
class ShoppingCartManagementApplicationServiceIT {

    @Autowired
    ShoppingCartManagementApplicationService shoppingCartManagementApplicationService;

    @MockitoBean
    ProductCatalogService productCatalogService;

    @Autowired
    ShoppingCarts shoppingCarts;

    @Autowired
    Customers customers;

    @Test
    void shouldAddItem() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customers.add(customer);

        Product product = ProductTestDataBuilder.aProduct()
            .id(new ProductId())
            .price(new Money("100"))
            .build();
        Mockito.when(productCatalogService.ofId(Mockito.any(ProductId.class)))
            .thenReturn(Optional.of(product));

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
            .withItems(false)
            .customerId(customer.id())
            .build();
        shoppingCarts.add(shoppingCart);

        Integer quantity = 2;
        shoppingCartManagementApplicationService.addItem(
            new ShoppingCartItemInput(quantity, product.id().value(), shoppingCart.id().value()));

        ShoppingCart updatedShoppingCart = shoppingCarts.ofId(shoppingCart.id()).orElse(null);

        Assertions.assertThat(updatedShoppingCart).isNotNull();
        Assertions.assertThat(updatedShoppingCart.findItem(product.id()).productId()).isEqualTo(product.id());
        Assertions.assertThat(updatedShoppingCart.totalItems()).isEqualTo(new Quantity(2));
        Assertions.assertThat(updatedShoppingCart.totalAmount()).isEqualTo(new Money("200"));
    }

    @Test
    void givenANonExistentShoppingCart_whenAddItem_shouldThrowException() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customers.add(customer);

        Product product = ProductTestDataBuilder.aProduct().build();
        Mockito.when(productCatalogService.ofId(Mockito.any(ProductId.class)))
            .thenReturn(Optional.of(product));

        Integer quantity = 2;
        ShoppingCartId nonExistentShoppingCartId = new ShoppingCartId();
        Assertions.assertThatExceptionOfType(ShoppingCartNotFoundException.class)
            .isThrownBy(() -> shoppingCartManagementApplicationService.addItem(
                new ShoppingCartItemInput(
                quantity,
                product.id().value(),
                nonExistentShoppingCartId.value()))
            );
    }

    @Test
    void givenANonExistentProduct_whenAddItem_shouldThrowException() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customers.add(customer);

        Mockito.when(productCatalogService.ofId(Mockito.any(ProductId.class)))
            .thenReturn(Optional.empty());

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
            .withItems(false)
            .customerId(customer.id())
            .build();
        shoppingCarts.add(shoppingCart);

        Integer quantity = 2;
        Assertions.assertThatExceptionOfType(ProductNotFoundException.class)
            .isThrownBy(() -> shoppingCartManagementApplicationService.addItem(new ShoppingCartItemInput(
                quantity,
                new ProductId().value(),
                shoppingCart.id().value()))
            );
    }

    @Test
    void givenAProductOutOfStock_whenAddItem_shouldThrowException() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customers.add(customer);

        Product product = ProductTestDataBuilder.aProduct()
            .inStock(false)
            .build();
        Mockito.when(productCatalogService.ofId(Mockito.any(ProductId.class)))
            .thenReturn(Optional.of(product));

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
            .withItems(false)
            .customerId(customer.id())
            .build();
        shoppingCarts.add(shoppingCart);

        Integer quantity = 2;
        Assertions.assertThatExceptionOfType(ProductOutOfStockException.class)
            .isThrownBy(() -> shoppingCartManagementApplicationService.addItem(new ShoppingCartItemInput(
                quantity,
                product.id().value(),
                shoppingCart.id().value()))
            );
    }

    @Test
    void shouldCreateANewShoppingCart() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customers.add(customer);

        UUID shoppingCartId = shoppingCartManagementApplicationService.createNew(customer.id().value());

        ShoppingCart shoppingCart = shoppingCarts.ofId(new ShoppingCartId(shoppingCartId)).orElse(null);

        Assertions.assertThat(shoppingCart).isNotNull();
        Assertions.assertThat(shoppingCart.id()).isEqualTo(new ShoppingCartId(shoppingCartId));
        Assertions.assertThat(shoppingCart.customerId()).isEqualTo(customer.id());
        Assertions.assertThat(shoppingCart.totalAmount()).isEqualTo(Money.ZERO);
        Assertions.assertThat(shoppingCart.totalItems()).isEqualTo(Quantity.ZERO);
    }

    @Test
    void givenANonExistentCustomer_whenCreateANewShoppingCart_shouldThrowException() {
        CustomerId nonExistentCustomerId = new CustomerId();

        Assertions.assertThatExceptionOfType(CustomerNotFoundException.class)
            .isThrownBy(() -> shoppingCartManagementApplicationService.createNew(nonExistentCustomerId.value()));
    }

    @Test
    void givenAnCustomerWhoHasAShoppingCart_whenCreateANewShoppingCart_shouldThrowException() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customers.add(customer);

        shoppingCartManagementApplicationService.createNew(customer.id().value());

        Assertions.assertThatExceptionOfType(CustomerAlreadyHaveShoppingCartException.class)
            .isThrownBy(() -> shoppingCartManagementApplicationService.createNew(customer.id().value()));
    }

    @Test
    void shouldRemoveItem() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customers.add(customer);

        ShoppingCart newShoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
            .withItems(false)
            .customerId(customer.id())
            .build();

        Product product = ProductTestDataBuilder.aProduct().build();
        newShoppingCart.addItem(product, new Quantity(2));

        shoppingCarts.add(newShoppingCart);

        ShoppingCart savedShoppingCart = shoppingCarts.ofId(newShoppingCart.id()).orElseThrow();
        ShoppingCartItem shoppingCartItem = savedShoppingCart.findItem(product.id());
        shoppingCartManagementApplicationService.removeItem(
            shoppingCartItem.shoppingCartId().value(),
            shoppingCartItem.id().value());

        ShoppingCart updatedShoppingCart = shoppingCarts.ofId(newShoppingCart.id()).orElse(null);
        Assertions.assertThat(updatedShoppingCart).isNotNull();
        Assertions.assertThat(updatedShoppingCart.totalItems()).isEqualTo(Quantity.ZERO);
        Assertions.assertThat(updatedShoppingCart.totalAmount()).isEqualTo(Money.ZERO);
    }

    @Test
    void givenANonExistentShoppingCart_whenRemoveItem_shouldThrowException() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customers.add(customer);

        ShoppingCart newShoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
            .withItems(false)
            .customerId(customer.id())
            .build();

        Product product = ProductTestDataBuilder.aProduct().build();
        newShoppingCart.addItem(product, new Quantity(2));

        shoppingCarts.add(newShoppingCart);

        ShoppingCart savedShoppingCart = shoppingCarts.ofId(newShoppingCart.id()).orElseThrow();
        ShoppingCartItem shoppingCartItem = savedShoppingCart.findItem(product.id());

        ShoppingCartId nonExistentShoppingCartId = new ShoppingCartId();
        Assertions.assertThatExceptionOfType(ShoppingCartNotFoundException.class)
            .isThrownBy(() -> shoppingCartManagementApplicationService.removeItem(
                nonExistentShoppingCartId.value(),
                shoppingCartItem.id().value())
            );
    }

    @Test
    void givenANonExistentShoppingCartItem_whenRemoveItem_shouldThrowException() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customers.add(customer);

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
            .withItems(false)
            .customerId(customer.id())
            .build();
        shoppingCarts.add(shoppingCart);

        ShoppingCartItemId nonExistentShoppingCartItemId = new ShoppingCartItemId();
        Assertions.assertThatExceptionOfType(ShoppingCartDoesNotContainItemException.class)
            .isThrownBy(() -> shoppingCartManagementApplicationService.removeItem(
                shoppingCart.id().value(),
                nonExistentShoppingCartItemId.value())
            );
    }

    @Test
    void shouldEmptyShoppingCart() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customers.add(customer);

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
            .withItems(true)
            .customerId(customer.id())
            .build();
        shoppingCarts.add(shoppingCart);

        shoppingCartManagementApplicationService.empty(shoppingCart.id().value());

        ShoppingCart emptyShoppingCart = shoppingCarts.ofId(shoppingCart.id()).orElse(null);
        Assertions.assertThat(emptyShoppingCart).isNotNull();
        Assertions.assertThat(emptyShoppingCart.totalItems()).isEqualTo(Quantity.ZERO);
        Assertions.assertThat(emptyShoppingCart.totalAmount()).isEqualTo(Money.ZERO);
    }

    @Test
    void givenANonExistentShoppingCart_whenEmptyShoppingCart_shouldThrowException() {
        ShoppingCartId nonExistentShoppingCartId = new ShoppingCartId();
        Assertions.assertThatExceptionOfType(ShoppingCartNotFoundException.class)
            .isThrownBy(() -> shoppingCartManagementApplicationService.empty(nonExistentShoppingCartId.value()));
    }

    @Test
    void shouldDeleteShoppingCart() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customers.add(customer);

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
            .customerId(customer.id())
            .build();
        shoppingCarts.add(shoppingCart);

        shoppingCartManagementApplicationService.delete(shoppingCart.id().value());

        Optional<ShoppingCart> possibleShoppingCart = shoppingCarts.ofId(shoppingCart.id());
        Assertions.assertThat(possibleShoppingCart).isNotPresent();
    }

    @Test
    void givenANonExistentShoppingCart_whenDeleteShoppingCart_shouldThrowException() {
        ShoppingCartId nonExistentShoppingCartId = new ShoppingCartId();
        Assertions.assertThatExceptionOfType(ShoppingCartNotFoundException.class)
            .isThrownBy(() -> shoppingCartManagementApplicationService.delete(nonExistentShoppingCartId.value()));
    }

}