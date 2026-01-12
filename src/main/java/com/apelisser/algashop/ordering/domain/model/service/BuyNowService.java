package com.apelisser.algashop.ordering.domain.model.service;

import com.apelisser.algashop.ordering.domain.model.entity.Order;
import com.apelisser.algashop.ordering.domain.model.entity.PaymentMethod;
import com.apelisser.algashop.ordering.domain.model.utility.DomainService;
import com.apelisser.algashop.ordering.domain.model.valueobject.Billing;
import com.apelisser.algashop.ordering.domain.model.valueobject.Product;
import com.apelisser.algashop.ordering.domain.model.valueobject.Quantity;
import com.apelisser.algashop.ordering.domain.model.valueobject.Shipping;
import com.apelisser.algashop.ordering.domain.model.valueobject.id.CustomerId;

@DomainService
public class BuyNowService {

    public Order buyNow(Product product, CustomerId customerId, Billing billing, Shipping shipping, Quantity quantity,
            PaymentMethod paymentMethod) {
        Order order = Order.draft(customerId);
        order.changePaymentMethod(paymentMethod);
        order.changeBilling(billing);
        order.changeShipping(shipping);
        order.addItem(product, quantity);
        order.place();

        return order;
    }

}
