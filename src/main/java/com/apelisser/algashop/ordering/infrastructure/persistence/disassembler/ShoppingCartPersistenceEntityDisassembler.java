package com.apelisser.algashop.ordering.infrastructure.persistence.disassembler;

import com.apelisser.algashop.ordering.domain.model.entity.ShoppingCart;
import com.apelisser.algashop.ordering.domain.model.entity.ShoppingCartItem;
import com.apelisser.algashop.ordering.domain.model.valueobject.Money;
import com.apelisser.algashop.ordering.domain.model.valueobject.ProductName;
import com.apelisser.algashop.ordering.domain.model.valueobject.Quantity;
import com.apelisser.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.apelisser.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.apelisser.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;
import com.apelisser.algashop.ordering.domain.model.valueobject.id.ShoppingCartItemId;
import com.apelisser.algashop.ordering.infrastructure.persistence.entity.ShoppingCartItemPersistenceEntity;
import com.apelisser.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceEntity;
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
