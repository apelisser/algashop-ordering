package com.apelisser.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import com.apelisser.algashop.ordering.domain.model.commons.Money;
import com.apelisser.algashop.ordering.domain.model.product.ProductName;
import com.apelisser.algashop.ordering.domain.model.commons.Quantity;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerId;
import com.apelisser.algashop.ordering.domain.model.product.ProductId;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCartItemId;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ShoppingCartPersistenceEntityDisassembler {

    public ShoppingCart toDomainEntity(ShoppingCartPersistenceEntity persistenceEntity) {
        return ShoppingCart.existing()
            .id(new ShoppingCartId(persistenceEntity.getId()))
            .customerId(new CustomerId(persistenceEntity.getCustomerId()))
            .totalAmount(new Money(persistenceEntity.getTotalAmount()))
            .createdAt(persistenceEntity.getCreatedAt())
            .items(toItemsDomainEntities(persistenceEntity.getItems()))
            .totalItems(new Quantity(persistenceEntity.getTotalItems()))
            .build();
    }

    private Set<ShoppingCartItem> toItemsDomainEntities(Set<ShoppingCartItemPersistenceEntity> source) {
        return source.stream()
            .map(this::toItemEntity)
            .collect(Collectors.toSet());
    }

    private ShoppingCartItem toItemEntity(ShoppingCartItemPersistenceEntity source) {
        return ShoppingCartItem.existing()
            .id(new ShoppingCartItemId(source.getId()))
            .shoppingCartId(new ShoppingCartId(source.getShoppingCartId()))
            .productId(new ProductId(source.getProductId()))
            .productName(new ProductName(source.getName()))
            .price(new Money(source.getPrice()))
            .quantity(new Quantity(source.getQuantity()))
            .available(source.getAvailable())
            .totalAmount(new Money(source.getTotalAmount()))
            .build();
    }

}
