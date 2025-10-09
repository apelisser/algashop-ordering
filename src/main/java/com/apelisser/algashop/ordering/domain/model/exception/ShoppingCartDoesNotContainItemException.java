package com.apelisser.algashop.ordering.domain.model.exception;

import com.apelisser.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.apelisser.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;
import com.apelisser.algashop.ordering.domain.model.valueobject.id.ShoppingCartItemId;

public class ShoppingCartDoesNotContainItemException extends DomainException {

    public ShoppingCartDoesNotContainItemException(ShoppingCartId shoppingCartId, ShoppingCartItemId shoppingCartItemId) {
        super(String.format(ErrorMessages.ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_ITEM, shoppingCartId, shoppingCartItemId));
    }

    public ShoppingCartDoesNotContainItemException(ShoppingCartId shoppingCartId, ProductId productId) {
        super(String.format(ErrorMessages.ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_PRODUCT, shoppingCartId, productId));
    }

}
