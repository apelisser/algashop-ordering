package com.apelisser.algashop.ordering.domain.model.exception;

import com.apelisser.algashop.ordering.domain.model.valueobject.id.ProductId;

public class ShoppingCartItemIncompatibleProductException extends DomainException {

    public ShoppingCartItemIncompatibleProductException(ProductId existingProductId, ProductId newProductId) {
        super(String.format(ErrorMessages.ERROR_SHOPPING_CART_ITEM_INCOMPATIBLE_PRODUCT, existingProductId, newProductId));
    }

}
