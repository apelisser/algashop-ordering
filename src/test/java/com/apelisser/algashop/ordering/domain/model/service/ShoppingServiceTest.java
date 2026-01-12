package com.apelisser.algashop.ordering.domain.model.service;

import com.apelisser.algashop.ordering.domain.model.entity.CustomerTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.entity.ShoppingCart;
import com.apelisser.algashop.ordering.domain.model.entity.ShoppingCartTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.exception.CustomerAlreadyHaveShoppingCartException;
import com.apelisser.algashop.ordering.domain.model.exception.CustomerNotFoundException;
import com.apelisser.algashop.ordering.domain.model.repository.Customers;
import com.apelisser.algashop.ordering.domain.model.repository.ShoppingCarts;
import com.apelisser.algashop.ordering.domain.model.valueobject.Money;
import com.apelisser.algashop.ordering.domain.model.valueobject.Quantity;
import com.apelisser.algashop.ordering.domain.model.valueobject.id.CustomerId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ShoppingServiceTest {

    @Mock
    Customers customers;

    @Mock
    ShoppingCarts shoppingCarts;

    @InjectMocks
    ShoppingService shoppingService;

    @Test
    void givenACustomerWithoutAShoppingCart_whenStartShopping_shouldCreateANewShoppingCart() {
        Mockito.when(customers.exists(Mockito.any(CustomerId.class))).thenReturn(true);
        Mockito.when(shoppingCarts.ofCustomer(Mockito.any(CustomerId.class))).thenReturn(Optional.empty());

        CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;
        ShoppingCart shoppingCart = shoppingService.startShopping(customerId);

        Mockito.verify(customers).exists(customerId);
        Mockito.verify(shoppingCarts).ofCustomer(customerId);

        Assertions.assertThat(shoppingCart).isNotNull();
        Assertions.assertThat(shoppingCart.customerId()).isEqualTo(customerId);
        Assertions.assertThat(shoppingCart.isEmpty()).isTrue();
        Assertions.assertThat(shoppingCart.totalItems()).isEqualTo(Quantity.ZERO);
        Assertions.assertThat(shoppingCart.totalAmount()).isEqualTo(Money.ZERO);
    }

    @Test
    void givenANonExistentCustomer_whenStartShopping_shouldThrowException() {
        Mockito.when(customers.exists(Mockito.any(CustomerId.class))).thenReturn(false);

        CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;

        Assertions.assertThatExceptionOfType(CustomerNotFoundException.class)
            .isThrownBy(() -> shoppingService.startShopping(customerId));

        Mockito.verify(customers, Mockito.times(1)).exists(customerId);
        Mockito.verify(shoppingCarts, Mockito.never()).ofCustomer(customerId);
    }


    @Test
    void givenACustomerAlreadyHasAShoppingCart_whenStartShopping_shouldThrowException() {
        Mockito.when(customers.exists(Mockito.any(CustomerId.class))).thenReturn(true);
        Mockito.when(shoppingCarts.ofCustomer(Mockito.any(CustomerId.class)))
            .thenReturn(Optional.of(ShoppingCartTestDataBuilder.aShoppingCart().build()));

        CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;

        Assertions.assertThatExceptionOfType(CustomerAlreadyHaveShoppingCartException.class)
            .isThrownBy(() -> shoppingService.startShopping(customerId));

        Mockito.verify(customers).exists(customerId);
        Mockito.verify(shoppingCarts).ofCustomer(customerId);
    }

}