package com.apelisser.algashop.ordering.domain.model.order;

import com.apelisser.algashop.ordering.domain.model.customer.Customer;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.apelisser.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.product.ProductOutOfStockException;
import com.apelisser.algashop.ordering.domain.model.commons.Money;
import com.apelisser.algashop.ordering.domain.model.product.Product;
import com.apelisser.algashop.ordering.domain.model.commons.Quantity;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;

@ExtendWith(MockitoExtension.class)
class BuyNowServiceTest {

    @InjectMocks
    BuyNowService buyNowService;

    @Mock
    Orders orders;

    @Test
    void givenValidPurchaseData_whenBuyNow_shouldCreateAnOrder() {
        Product product = ProductTestDataBuilder.aProduct().build();
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        Quantity quantity = new Quantity(2);
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        Money expectedTotalAmount = product.price().multiply(quantity).add(shipping.cost());

        Order order = buyNowService.buyNow(product, customer, billing, shipping, quantity, paymentMethod);
        Assertions.assertThat(order).isNotNull();
        Assertions.assertThat(order.isPlaced()).isTrue();
        Assertions.assertThat(order.customerId()).isEqualTo(customer.id());
        Assertions.assertThat(order.paymentMethod()).isEqualTo(paymentMethod);
        Assertions.assertThat(order.totalItems()).isEqualTo(quantity);
        Assertions.assertThat(order.billing()).isEqualTo(billing);
        Assertions.assertThat(order.shipping()).isEqualTo(shipping);
        Assertions.assertThat(order.totalAmount()).isEqualTo(expectedTotalAmount);
    }

    @Test
    void givenAnUnavailableItem_whenBuyNow_shouldThrowException() {
        Product unavailableProduct = ProductTestDataBuilder.aProduct().inStock(false).build();

        Assertions.assertThatExceptionOfType(ProductOutOfStockException.class)
            .isThrownBy(() -> buyNowService.buyNow(unavailableProduct,
                CustomerTestDataBuilder.existingCustomer().build(),
                OrderTestDataBuilder.aBilling(),
                OrderTestDataBuilder.aShipping(),
                new Quantity(2),
                PaymentMethod.CREDIT_CARD));
    }

    @Test
    void givenAnInvalidQuantity_whenBuyNow_shouldThrowException() {
        Quantity invalidQuantity = Quantity.ZERO;

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> buyNowService.buyNow(
                ProductTestDataBuilder.aProduct().build(),
                CustomerTestDataBuilder.existingCustomer().build(),
                OrderTestDataBuilder.aBilling(),
                OrderTestDataBuilder.aShipping(),
                invalidQuantity,
                PaymentMethod.CREDIT_CARD));
    }

    @Test
    void givenCustomerWithFreeShipping_whenBuyNow_shouldCreateAnOrderWithFreeShipping() {
        Mockito.when(orders.salesQuantityByCustomerInYear(Mockito.any(CustomerId.class), Mockito.any(Year.class)))
            .thenReturn(2L);

        Product product = ProductTestDataBuilder.aProduct().build();
        Customer customer = CustomerTestDataBuilder.existingCustomer().loyaltyPoints(new LoyaltyPoints(100)).build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        Quantity quantity = new Quantity(2);
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        Money expectedTotalAmount = product.price().multiply(quantity);
        Shipping expectedShipping = shipping.toBuilder().cost(Money.ZERO).build();

        Order order = buyNowService.buyNow(product, customer, billing, shipping, quantity, paymentMethod);
        Assertions.assertThat(order).isNotNull();
        Assertions.assertThat(order.isPlaced()).isTrue();
        Assertions.assertThat(order.customerId()).isEqualTo(customer.id());
        Assertions.assertThat(order.paymentMethod()).isEqualTo(paymentMethod);
        Assertions.assertThat(order.totalItems()).isEqualTo(quantity);
        Assertions.assertThat(order.billing()).isEqualTo(billing);
        Assertions.assertThat(order.shipping()).isEqualTo(expectedShipping);
        Assertions.assertThat(order.totalAmount()).isEqualTo(expectedTotalAmount);
    }

}