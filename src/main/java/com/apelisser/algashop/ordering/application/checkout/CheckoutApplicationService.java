package com.apelisser.algashop.ordering.application.checkout;

import com.apelisser.algashop.ordering.domain.model.commons.ZipCode;
import com.apelisser.algashop.ordering.domain.model.customer.Customer;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.apelisser.algashop.ordering.domain.model.customer.Customers;
import com.apelisser.algashop.ordering.domain.model.order.Billing;
import com.apelisser.algashop.ordering.domain.model.order.CheckoutService;
import com.apelisser.algashop.ordering.domain.model.order.Order;
import com.apelisser.algashop.ordering.domain.model.order.Orders;
import com.apelisser.algashop.ordering.domain.model.order.PaymentMethod;
import com.apelisser.algashop.ordering.domain.model.order.Shipping;
import com.apelisser.algashop.ordering.domain.model.order.shipping.OriginAddressService;
import com.apelisser.algashop.ordering.domain.model.order.shipping.ShippingCostService;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCarts;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class CheckoutApplicationService {

    private final ShoppingCarts shoppingCarts;
    private final ShippingCostService shippingCostService;
    private final OriginAddressService originAddressService;
    private final CheckoutService checkoutService;
    private final ShippingInputDisassembler shippingInputDisassembler;
    private final BillingInputDisassembler billingInputDisassembler;
    private final Orders orders;
    private final Customers customers;

    public CheckoutApplicationService(ShoppingCarts shoppingCarts, ShippingCostService shippingCostService,
            OriginAddressService originAddressService, CheckoutService checkoutService,
            ShippingInputDisassembler shippingInputDisassembler, BillingInputDisassembler billingInputDisassembler,
            Orders orders, Customers customers) {
        this.shoppingCarts = shoppingCarts;
        this.shippingCostService = shippingCostService;
        this.originAddressService = originAddressService;
        this.checkoutService = checkoutService;
        this.shippingInputDisassembler = shippingInputDisassembler;
        this.billingInputDisassembler = billingInputDisassembler;
        this.orders = orders;
        this.customers = customers;
    }

    @Transactional
    public String checkout(CheckoutInput input) {
        Objects.requireNonNull(input);

        PaymentMethod paymentMethod = PaymentMethod.valueOf(input.getPaymentMethod());

        ShoppingCart shoppingCart = shoppingCarts.ofId(new ShoppingCartId(input.getShoppingCartId()))
            .orElseThrow(ShoppingCartNotFoundException::new);

        Billing billing = billingInputDisassembler.toDomainModel(input.getBilling());

        Customer customer = customers.ofId(shoppingCart.customerId()).orElseThrow(CustomerNotFoundException::new);

        ShippingCostService.CalculationResult shippingCost = this.calculateShippingCost(input.getShipping());
        Shipping shipping = shippingInputDisassembler.toDomainModel(input.getShipping(), shippingCost);

        Order order = checkoutService.checkout(customer, shoppingCart, billing, shipping, paymentMethod);

        orders.add(order);
        shoppingCarts.add(shoppingCart);

        return order.id().toString();
    }

    private ShippingCostService.CalculationResult calculateShippingCost(ShippingInput input) {
        ZipCode origin = originAddressService.originAddress().zipCode();
        ZipCode destination = new ZipCode(input.getAddress().getZipCode());
        var request = new ShippingCostService.CalculationRequest(origin, destination);
        return shippingCostService.calculate(request);
    }

}
