package com.apelisser.algashop.ordering.application.checkout;

import com.apelisser.algashop.ordering.domain.model.commons.Quantity;
import com.apelisser.algashop.ordering.domain.model.commons.ZipCode;
import com.apelisser.algashop.ordering.domain.model.customer.Customer;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerId;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.apelisser.algashop.ordering.domain.model.customer.Customers;
import com.apelisser.algashop.ordering.domain.model.order.Billing;
import com.apelisser.algashop.ordering.domain.model.order.BuyNowService;
import com.apelisser.algashop.ordering.domain.model.order.Order;
import com.apelisser.algashop.ordering.domain.model.order.Orders;
import com.apelisser.algashop.ordering.domain.model.order.PaymentMethod;
import com.apelisser.algashop.ordering.domain.model.order.Shipping;
import com.apelisser.algashop.ordering.domain.model.order.shipping.OriginAddressService;
import com.apelisser.algashop.ordering.domain.model.order.shipping.ShippingCostService;
import com.apelisser.algashop.ordering.domain.model.product.Product;
import com.apelisser.algashop.ordering.domain.model.product.ProductCatalogService;
import com.apelisser.algashop.ordering.domain.model.product.ProductId;
import com.apelisser.algashop.ordering.domain.model.product.ProductNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class BuyNowApplicationService {

    private final BuyNowService buyNowService;
    private final ProductCatalogService productCatalogService;
    private final ShippingCostService shippingCostService;
    private final OriginAddressService originAddressService;
    private final Orders orders;
    private final Customers customers;
    private final ShippingInputDisassembler shippingInputDisassembler;
    private final BillingInputDisassembler billingInputDisassembler;

    public BuyNowApplicationService(BuyNowService buyNowService, ProductCatalogService productCatalogService,
            ShippingCostService shippingCostService, OriginAddressService originAddressService, Orders orders, Customers customers,
            ShippingInputDisassembler shippingInputDisassembler, BillingInputDisassembler billingInputDisassembler) {
        this.buyNowService = buyNowService;
        this.productCatalogService = productCatalogService;
        this.shippingCostService = shippingCostService;
        this.originAddressService = originAddressService;
        this.orders = orders;
        this.customers = customers;
        this.shippingInputDisassembler = shippingInputDisassembler;
        this.billingInputDisassembler = billingInputDisassembler;
    }

    @Transactional
    public String buyNow(BuyNowInput input) {
        Objects.requireNonNull(input);

        PaymentMethod paymentMethod = PaymentMethod.valueOf(input.getPaymentMethod());
        CustomerId customerId = new CustomerId(input.getCustomerId());
        Quantity quantity = new Quantity(input.getQuantity());

        Customer customer = customers.ofId(customerId).orElseThrow(CustomerNotFoundException::new);
        Product product = findProduct(new ProductId(input.getProductId()));
        ShippingCostService.CalculationResult shippingCalculationResult = calculateShippingCost(input.getShipping());

        Shipping shipping = shippingInputDisassembler.toDomainModel(input.getShipping(), shippingCalculationResult);
        Billing billing = billingInputDisassembler.toDomainModel(input.getBilling());


        Order order = buyNowService.buyNow(product, customer, billing, shipping, quantity, paymentMethod);

        orders.add(order);

        return order.id().toString();
    }

    private Product findProduct(ProductId productId) {
        return productCatalogService.ofId(productId).orElseThrow(ProductNotFoundException::new);
    }

    public ShippingCostService.CalculationResult calculateShippingCost(ShippingInput shipping) {
        ZipCode origin = originAddressService.originAddress().zipCode();
        ZipCode destination = new ZipCode(shipping.getAddress().getZipCode());

        return shippingCostService.calculate(new ShippingCostService.CalculationRequest(origin, destination));
    }

}
