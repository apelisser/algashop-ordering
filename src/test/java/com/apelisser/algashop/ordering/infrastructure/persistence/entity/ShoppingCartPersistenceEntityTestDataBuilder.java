package com.apelisser.algashop.ordering.infrastructure.persistence.entity;

import com.apelisser.algashop.ordering.domain.model.utility.IdGenerator;
import com.apelisser.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceEntity.ShoppingCartPersistenceEntityBuilder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;


public final class ShoppingCartPersistenceEntityTestDataBuilder {

    private ShoppingCartPersistenceEntityTestDataBuilder() {
        throw new AssertionError("Utility class");
    }

    public static ShoppingCartPersistenceEntityBuilder existingShoppingCart() {
        return ShoppingCartPersistenceEntity.builder()
            .id(IdGenerator.generateTimeBasedUUID())
            .customer(CustomerPersistenceEntityTestDataBuilder.existingCustomer().build())
            .totalItems(3)
            .totalAmount(new BigDecimal(1250))
            .createdAt(OffsetDateTime.now())
            .items(Set.of(
                existingItem().build(),
                existingItemAlt().build()
            ));
    }

    public static ShoppingCartItemPersistenceEntity.ShoppingCartItemPersistenceEntityBuilder existingItem() {
        return ShoppingCartItemPersistenceEntity.builder()
            .id(IdGenerator.generateTimeBasedUUID())
            .price(new BigDecimal(500))
            .quantity(2)
            .totalAmount(new BigDecimal(1000))
            .name("Notebook")
            .productId(IdGenerator.generateTimeBasedUUID());
    }

    public static ShoppingCartItemPersistenceEntity.ShoppingCartItemPersistenceEntityBuilder existingItemAlt() {
        return ShoppingCartItemPersistenceEntity.builder()
            .id(IdGenerator.generateTimeBasedUUID())
            .price(new BigDecimal(250))
            .quantity(1)
            .totalAmount(new BigDecimal(250))
            .name("Mouse pad")
            .productId(IdGenerator.generateTimeBasedUUID());
    }

}