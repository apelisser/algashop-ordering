package com.apelisser.algashop.ordering.core.domain.model.shoppingcart;

import com.apelisser.algashop.ordering.core.domain.model.customer.CustomerAlreadyHaveShoppingCartException;
import com.apelisser.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;
import com.apelisser.algashop.ordering.core.domain.model.customer.Customers;
import com.apelisser.algashop.ordering.core.domain.model.DomainService;
import com.apelisser.algashop.ordering.core.domain.model.customer.CustomerId;

import java.util.Objects;

@DomainService
public class ShoppingService {

    private final Customers customers;
    private final ShoppingCarts shoppingCarts;

    public ShoppingService(Customers customers, ShoppingCarts shoppingCarts) {
        this.customers = customers;
        this.shoppingCarts = shoppingCarts;
    }

    public ShoppingCart startShopping(CustomerId customerId) {
        Objects.requireNonNull(customerId);

        this.checkIfCustomerExists(customerId);
        this.checkIfCustomerAlreadyHasAShoppingCart(customerId);

        return ShoppingCart.startShopping(customerId);
    }

    private void checkIfCustomerExists(CustomerId customerId) {
        if (!customers.exists(customerId)) {
            throw new CustomerNotFoundException(customerId);
        }
    }

    private void checkIfCustomerAlreadyHasAShoppingCart(CustomerId customerId) {
        if (shoppingCarts.ofCustomer(customerId).isPresent()) {
            throw new CustomerAlreadyHaveShoppingCartException(customerId);
        }
    }

}
