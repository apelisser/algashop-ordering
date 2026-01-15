package com.apelisser.algashop.ordering.domain.model.order;

import com.apelisser.algashop.ordering.domain.model.DomainService;
import com.apelisser.algashop.ordering.domain.model.product.Product;
import com.apelisser.algashop.ordering.domain.model.commons.Quantity;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerId;

import java.util.Objects;

@DomainService
public class BuyNowService {

    public Order buyNow(Product product, CustomerId customerId, Billing billing, Shipping shipping, Quantity quantity,
            PaymentMethod paymentMethod) {
        Objects.requireNonNull(product);
        Objects.requireNonNull(customerId);
        Objects.requireNonNull(billing);
        Objects.requireNonNull(shipping);
        Objects.requireNonNull(quantity);
        Objects.requireNonNull(paymentMethod);

        product.checkOutOfStock();

        Order order = Order.draft(customerId);
        order.changePaymentMethod(paymentMethod);
        order.changeBilling(billing);
        order.changeShipping(shipping);
        order.addItem(product, quantity);
        order.place();

        return order;
    }

}
