package com.apelisser.algashop.ordering.core.application.shoppingcart;

import com.apelisser.algashop.ordering.core.ports.in.shoppingcart.ForQueryingShoppingCarts;
import com.apelisser.algashop.ordering.core.ports.in.shoppingcart.ShoppingCartOutput;
import com.apelisser.algashop.ordering.core.ports.out.shoppingcart.ForObtainingShoppingCarts;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ShoppingCartQueryService implements ForQueryingShoppingCarts {

    private final ForObtainingShoppingCarts forObtainingShoppingCarts;

    public ShoppingCartQueryService(ForObtainingShoppingCarts forObtainingShoppingCarts) {
        this.forObtainingShoppingCarts = forObtainingShoppingCarts;
    }

    @Override
    public ShoppingCartOutput findById(UUID shoppingCartId) {
        return forObtainingShoppingCarts.findById(shoppingCartId);
    }

    @Override
    public ShoppingCartOutput findByCustomerId(UUID customerId) {
        return forObtainingShoppingCarts.findByCustomerId(customerId);
    }

}
