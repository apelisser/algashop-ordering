package com.apelisser.algashop.ordering.infrastructure.persistence.assembler;

import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.apelisser.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import com.apelisser.algashop.ordering.infrastructure.persistence.entity.ShoppingCartItemPersistenceEntity;
import com.apelisser.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceEntity;
import com.apelisser.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ShoppingCartPersistenceEntityAssembler {

    private final CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

    public ShoppingCartPersistenceEntityAssembler(CustomerPersistenceEntityRepository customerPersistenceEntityRepository) {
        this.customerPersistenceEntityRepository = customerPersistenceEntityRepository;
    }

    public ShoppingCartPersistenceEntity fromDomain(ShoppingCart shoppingCart) {
        return merge(new ShoppingCartPersistenceEntity(), shoppingCart);
    }

    public ShoppingCartPersistenceEntity merge(ShoppingCartPersistenceEntity persistenceEntity, ShoppingCart shoppingCart) {
        persistenceEntity.setId(shoppingCart.id().value());
        persistenceEntity.setCustomer(customerPersistenceEntityRepository.getReferenceById(shoppingCart.customerId().value()));
        persistenceEntity.setTotalAmount(shoppingCart.totalAmount().value());
        persistenceEntity.setTotalItems(shoppingCart.totalItems().value());
        persistenceEntity.setCreatedAt(shoppingCart.createdAt());
        persistenceEntity.replaceItems(toShoppingCartItemsEntities(shoppingCart.items()));

        return persistenceEntity;
    }

    private Set<ShoppingCartItemPersistenceEntity> toShoppingCartItemsEntities(Set<ShoppingCartItem> items) {
        return items.stream()
            .map(item -> this.mergeItem(new ShoppingCartItemPersistenceEntity(), item))
            .collect(Collectors.toSet());
    }

    private ShoppingCartItemPersistenceEntity mergeItem(ShoppingCartItemPersistenceEntity persistenceEntity,
            ShoppingCartItem shoppingCartItem) {
        persistenceEntity.setId(shoppingCartItem.id().value());
        persistenceEntity.setProductId(shoppingCartItem.productId().value());
        persistenceEntity.setName(shoppingCartItem.name().value());
        persistenceEntity.setPrice(shoppingCartItem.price().value());
        persistenceEntity.setQuantity(shoppingCartItem.quantity().value());
        persistenceEntity.setAvailable(shoppingCartItem.isAvailable());
        persistenceEntity.setTotalAmount(shoppingCartItem.totalAmount().value());

        return persistenceEntity;
    }

    private ShoppingCartItemPersistenceEntity toShoppingCartItemEntity(ShoppingCartItem item) {
        return ShoppingCartItemPersistenceEntity.builder()
            .id(item.id().value())
            .shoppingCart(ShoppingCartPersistenceEntity.builder().id(item.shoppingCartId().value()).build())
            .productId(item.productId().value())
            .name(item.name().value())
            .price(item.price().value())
            .quantity(item.quantity().value())
            .available(item.isAvailable())
            .totalAmount(item.totalAmount().value())
            .build();
    }

}
