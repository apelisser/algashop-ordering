package com.apelisser.algashop.ordering.domain.model.order;

import com.apelisser.algashop.ordering.domain.model.DomainService;
import com.apelisser.algashop.ordering.domain.model.commons.Money;
import com.apelisser.algashop.ordering.domain.model.customer.Customer;
import com.apelisser.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.apelisser.algashop.ordering.domain.model.product.Product;
import com.apelisser.algashop.ordering.domain.model.commons.Quantity;

import java.time.Year;
import java.util.Objects;

@DomainService
public class BuyNowService {

    private final CustomerHaveFreeShippingSpecification customerHaveFreeShippingSpecification;

    public BuyNowService(Orders orders, CustomerHaveFreeShippingSpecification customerHaveFreeShippingSpecification) {
        this.customerHaveFreeShippingSpecification = customerHaveFreeShippingSpecification;
    }

    public Order buyNow(Product product, Customer customer, Billing billing, Shipping shipping, Quantity quantity,
            PaymentMethod paymentMethod) {
        Objects.requireNonNull(product);
        Objects.requireNonNull(customer);
        Objects.requireNonNull(billing);
        Objects.requireNonNull(shipping);
        Objects.requireNonNull(quantity);
        Objects.requireNonNull(paymentMethod);

        product.checkOutOfStock();

        Order order = Order.draft(customer.id());
        order.changePaymentMethod(paymentMethod);
        order.changeBilling(billing);
        order.addItem(product, quantity);

        if (haveFreeShipping(customer)) {
            Shipping freeShipping = shipping.toBuilder().cost(Money.ZERO).build();
            order.changeShipping(freeShipping);
        } else {
            order.changeShipping(shipping);
        }
        order.place();

        return order;
    }

    private boolean haveFreeShipping(Customer customer) {
        return customerHaveFreeShippingSpecification.isSatisfiedBy(customer);
    }

}
