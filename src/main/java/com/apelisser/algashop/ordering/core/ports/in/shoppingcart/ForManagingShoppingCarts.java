package com.apelisser.algashop.ordering.core.ports.in.shoppingcart;

import java.util.UUID;

public interface ForManagingShoppingCarts {

    void addItem(ShoppingCartItemInput input);

    UUID createNew(UUID customerId);

    void removeItem(UUID shoppingCartId, UUID shoppingCartItemId);

    void empty(UUID shoppingCartId);

    void delete(UUID shoppingCartId);

}
