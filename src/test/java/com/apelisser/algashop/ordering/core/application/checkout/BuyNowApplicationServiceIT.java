package com.apelisser.algashop.ordering.core.application.checkout;

import com.apelisser.algashop.ordering.core.application.AbstractApplicationIT;
import com.apelisser.algashop.ordering.core.domain.model.commons.Money;
import com.apelisser.algashop.ordering.core.domain.model.customer.CustomerTestDataBuilder;
import com.apelisser.algashop.ordering.core.domain.model.customer.Customers;
import com.apelisser.algashop.ordering.core.domain.model.order.OrderId;
import com.apelisser.algashop.ordering.core.domain.model.order.Orders;
import com.apelisser.algashop.ordering.core.domain.model.order.shipping.ShippingCostService;
import com.apelisser.algashop.ordering.core.domain.model.product.Product;
import com.apelisser.algashop.ordering.core.domain.model.product.ProductCatalogService;
import com.apelisser.algashop.ordering.core.domain.model.product.ProductTestDataBuilder;
import com.apelisser.algashop.ordering.core.ports.in.checkout.BuyNowInput;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Optional;

class BuyNowApplicationServiceIT extends AbstractApplicationIT {

    @Autowired
    BuyNowApplicationService buyNowApplicationService;

    @Autowired
    Orders orders;

    @Autowired
    Customers customers;

    @MockitoBean
    ProductCatalogService productCatalogService;

    @MockitoBean
    ShippingCostService shippingCostService;

    @BeforeEach
    void setUp() {
        if (!customers.exists(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)) {
            customers.add(CustomerTestDataBuilder.existingCustomer().build());
        }
    }

    @Test
    void shouldBuyNow() {
        Product product = ProductTestDataBuilder.aProduct().build();

        Mockito.when(productCatalogService.ofId(product.id())).thenReturn(Optional.of(product));
        Mockito.when(shippingCostService.calculate(Mockito.any(ShippingCostService.CalculationRequest.class)))
            .thenReturn(new ShippingCostService.CalculationResult(
                new Money("10.00"),
                LocalDate.now().plusDays(3)
            ));

        BuyNowInput input = BuyNowInputTestDataBuilder.aBuyNowInput().build();
        String orderId = buyNowApplicationService.buyNow(input);

        Assertions.assertThat(orderId).isNotBlank();
        Assertions.assertThat(orders.exists(new OrderId(orderId))).isTrue();
    }

}