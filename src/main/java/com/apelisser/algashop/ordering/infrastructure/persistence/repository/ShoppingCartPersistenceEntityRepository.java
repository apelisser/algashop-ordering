package com.apelisser.algashop.ordering.infrastructure.persistence.repository;

import com.apelisser.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface ShoppingCartPersistenceEntityRepository extends JpaRepository<ShoppingCartPersistenceEntity, UUID> {

    Optional<ShoppingCartPersistenceEntity> findByCustomer_Id(UUID value);

    @Modifying
    @Transactional
    @Query("""
        update
            ShoppingCartItemPersistenceEntity sci
        set
            sci.price = :price,
            sci.totalAmount = :price * sci.quantity
        where
            sci.productId = :productId
    """)
    void updateItemPrice(@Param("productId") UUID productId, @Param("price") BigDecimal price);

    @Modifying
    @Transactional
    @Query("""
        update
            ShoppingCartItemPersistenceEntity sci
        set
            sci.available = :available
        where
            sci.productId = :productId
    """)
    void updateItemAvailability(@Param("productId") UUID productId, @Param("available") boolean available);

    @Modifying
    @Transactional
    @Query("""
        update
            ShoppingCartPersistenceEntity sc
        set
            sc.totalAmount = (
                select sum(sci.totalAmount)
                from ShoppingCartItemPersistenceEntity sci
                where sci.shoppingCart.id = sc.id
            )
        where
            exists (
                select 1
                from ShoppingCartItemPersistenceEntity sci2
                where
                    sci2.shoppingCart.id = sc.id
                    and sci2.productId = :productId
            )
    """)
    void recalculateTotalsForCartsWithProduct(@Param("productId") UUID productId);

}
