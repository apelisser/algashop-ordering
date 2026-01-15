package com.apelisser.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.apelisser.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString(of = "id")
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "shopping_cart")
@EntityListeners(AuditingEntityListener.class)
public class ShoppingCartPersistenceEntity {

    @Id
    private UUID id;

    private BigDecimal totalAmount;
    private Integer totalItems;

    @JoinColumn
    @ManyToOne(optional = false)
    private CustomerPersistenceEntity customer;

    @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL)
    private Set<ShoppingCartItemPersistenceEntity> items = new HashSet<>();

    @CreatedBy
    private UUID createdByUserId;

    @CreatedDate
    private OffsetDateTime createdAt;

    @LastModifiedBy
    private UUID lastModifiedByUserId;

    @LastModifiedDate
    private OffsetDateTime lastModifiedAt;

    @Version
    private Long version;

    @Builder
    public ShoppingCartPersistenceEntity(UUID id, CustomerPersistenceEntity customer, BigDecimal totalAmount,
            Integer totalItems, Set<ShoppingCartItemPersistenceEntity> items, OffsetDateTime createdAt) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.totalItems = totalItems;
        this.customer = customer;
        this.createdAt = createdAt;
        this.replaceItems(items);
    }

    public void addItem(Set<ShoppingCartItemPersistenceEntity> items) {
        items.forEach(this::addItem);
    }

    public void addItem(ShoppingCartItemPersistenceEntity item) {
        if (item == null) {
            return;
        }

        if (this.getItems() == null) {
            this.setItems(new HashSet<>());
        }

        item.setShoppingCart(this);
        items.add(item);
    }

    public UUID getCustomerId() {
        return customer != null ? customer.getId() : null;
    }

    public void replaceItems(Set<ShoppingCartItemPersistenceEntity> updatedItems) {
        if (updatedItems == null || updatedItems.isEmpty()) {
            this.setItems(new HashSet<>());
            return;
        }

        updatedItems.forEach(item -> item.setShoppingCart(this));
        this.setItems(updatedItems);
    }

}
