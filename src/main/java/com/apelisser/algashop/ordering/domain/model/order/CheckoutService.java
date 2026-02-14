package com.apelisser.algashop.ordering.domain.model.order;

import com.apelisser.algashop.ordering.domain.model.commons.Money;
import com.apelisser.algashop.ordering.domain.model.customer.Customer;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCartCantProceedToCheckoutException;
import com.apelisser.algashop.ordering.domain.model.DomainService;
import com.apelisser.algashop.ordering.domain.model.product.Product;

import java.util.Objects;
import java.util.Set;

@DomainService
public class CheckoutService {

    private final CustomerHaveFreeShippingSpecification hasFreeShippingSpecification;

    public CheckoutService(CustomerHaveFreeShippingSpecification hasFreeShippingSpecification) {
        this.hasFreeShippingSpecification = hasFreeShippingSpecification;
    }

    public Order checkout(Customer customer, ShoppingCart shoppingCart, Billing billing, Shipping shipping,
            PaymentMethod paymentMethod) {
        Objects.requireNonNull(shoppingCart);
        Objects.requireNonNull(billing);
        Objects.requireNonNull(shipping);
        Objects.requireNonNull(paymentMethod);

        this.validateShoppingCart(shoppingCart);

        Order order = Order.draft(shoppingCart.customerId());
        order.changeBilling(billing);

        if (haveFreeShipping(customer)) {
            Shipping freeShipping = shipping.toBuilder().cost(Money.ZERO).build();
            order.changeShipping(freeShipping);
        } else {
            order.changeShipping(shipping);
        }

        order.changePaymentMethod(paymentMethod);

        this.addItemsToOrder(order, shoppingCart.items());

        order.place();
        shoppingCart.empty();

        return order;
    }

    private void validateShoppingCart(ShoppingCart shoppingCart) {
        Objects.requireNonNull(shoppingCart);
        if (shoppingCart.containsUnavailableItems() || shoppingCart.isEmpty()) {
            throw new ShoppingCartCantProceedToCheckoutException();
        }
    }

    private void addItemsToOrder(Order order, Set<ShoppingCartItem> items) {
        items.forEach(item -> {
            Product product = Product.builder()
                .id(item.productId())
                .name(item.name())
                .price(item.price())
                .inStock(item.isAvailable())
                .build();

            order.addItem(product, item.quantity());
        });
    }

    private boolean haveFreeShipping(Customer customer) {
        return this.hasFreeShippingSpecification.isSatisfiedBy(customer);
    }

}
