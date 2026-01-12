package com.apelisser.algashop.ordering.domain.model.service;

import com.apelisser.algashop.ordering.domain.model.entity.CustomerTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.entity.Order;
import com.apelisser.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.entity.PaymentMethod;
import com.apelisser.algashop.ordering.domain.model.entity.ProductTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.exception.ProductOutOfStockException;
import com.apelisser.algashop.ordering.domain.model.valueobject.Billing;
import com.apelisser.algashop.ordering.domain.model.valueobject.Money;
import com.apelisser.algashop.ordering.domain.model.valueobject.Product;
import com.apelisser.algashop.ordering.domain.model.valueobject.Quantity;
import com.apelisser.algashop.ordering.domain.model.valueobject.Shipping;
import com.apelisser.algashop.ordering.domain.model.valueobject.id.CustomerId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class BuyNowServiceTest {

    BuyNowService buyNowService = new BuyNowService();

    @Test
    void givenValidPurchaseData_whenBuyNow_shouldCreateAnOrder() {
        Product product = ProductTestDataBuilder.aProduct().build();
        CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        Quantity quantity = new Quantity(2);
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        Money expectedTotalAmount = product.price().multiply(quantity).add(shipping.cost());

        Order order = buyNowService.buyNow(product, customerId, billing, shipping, quantity, paymentMethod);
        Assertions.assertThat(order).isNotNull();
        Assertions.assertThat(order.isPlaced()).isTrue();
        Assertions.assertThat(order.customerId()).isEqualTo(customerId);
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
                CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID,
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
                CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID,
                OrderTestDataBuilder.aBilling(),
                OrderTestDataBuilder.aShipping(),
                invalidQuantity,
                PaymentMethod.CREDIT_CARD));
    }

}