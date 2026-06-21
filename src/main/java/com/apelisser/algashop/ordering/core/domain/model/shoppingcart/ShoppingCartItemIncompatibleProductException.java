package com.apelisser.algashop.ordering.core.domain.model.shoppingcart;

import com.apelisser.algashop.ordering.core.domain.model.DomainException;
import com.apelisser.algashop.ordering.core.domain.model.ErrorMessages;
import com.apelisser.algashop.ordering.core.domain.model.product.ProductId;

public class ShoppingCartItemIncompatibleProductException extends DomainException {

    public ShoppingCartItemIncompatibleProductException(ProductId existingProductId, ProductId newProductId) {
        super(String.format(ErrorMessages.ERROR_SHOPPING_CART_ITEM_INCOMPATIBLE_PRODUCT, existingProductId, newProductId));
    }

}
