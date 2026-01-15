package com.apelisser.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCartProductAdjustmentService;
import com.apelisser.algashop.ordering.domain.model.commons.Money;
import com.apelisser.algashop.ordering.domain.model.product.ProductId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ShoppingCartUpdateProvider implements ShoppingCartProductAdjustmentService {

    private final ShoppingCartPersistenceEntityRepository shoppingCartPersistenceEntityRepository;

    public ShoppingCartUpdateProvider(ShoppingCartPersistenceEntityRepository shoppingCartPersistenceEntityRepository) {
        this.shoppingCartPersistenceEntityRepository = shoppingCartPersistenceEntityRepository;
    }

    @Transactional
    @Override
    public void adjustPrice(ProductId productId, Money updatedPrice) {
        shoppingCartPersistenceEntityRepository.updateItemPrice(productId.value(), updatedPrice.value());
        shoppingCartPersistenceEntityRepository.recalculateTotalsForCartsWithProduct(productId.value());
    }

    @Transactional
    @Override
    public void changeAvailability(ProductId productId, boolean available) {
        shoppingCartPersistenceEntityRepository.updateItemAvailability(productId.value(), available);
    }

}
