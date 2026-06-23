package com.apelisser.algashop.ordering.infrastructure.config.auditing;

import com.apelisser.algashop.ordering.core.application.checkout.BuyNowApplicationService;
import com.apelisser.algashop.ordering.core.application.order.BillingInputDisassembler;
import com.apelisser.algashop.ordering.core.application.order.ShippingInputDisassembler;
import com.apelisser.algashop.ordering.core.domain.model.customer.Customers;
import com.apelisser.algashop.ordering.core.domain.model.order.BuyNowService;
import com.apelisser.algashop.ordering.core.domain.model.order.Orders;
import com.apelisser.algashop.ordering.core.domain.model.order.shipping.OriginAddressService;
import com.apelisser.algashop.ordering.core.domain.model.order.shipping.ShippingCostService;
import com.apelisser.algashop.ordering.core.domain.model.product.ProductCatalogService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfigurator {

    @Bean
    public BuyNowApplicationService buyNowApplicationService(
            BuyNowService buyNowService, ProductCatalogService productCatalogService,
            ShippingCostService shippingCostService, OriginAddressService originAddressService, Orders orders, Customers customers,
            ShippingInputDisassembler shippingInputDisassembler, BillingInputDisassembler billingInputDisassembler) {
        return new BuyNowApplicationService(
            buyNowService,
            productCatalogService,
            shippingCostService,
            originAddressService,
            orders,
            customers,
            shippingInputDisassembler,
            billingInputDisassembler
        );
    }

}
