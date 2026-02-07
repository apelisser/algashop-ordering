package com.apelisser.algashop.ordering.domain.model.shoppingcart;

import com.apelisser.algashop.ordering.domain.model.AbstractEventSourceEntity;
import com.apelisser.algashop.ordering.domain.model.AggregateRoot;
import com.apelisser.algashop.ordering.domain.model.commons.Money;
import com.apelisser.algashop.ordering.domain.model.product.Product;
import com.apelisser.algashop.ordering.domain.model.commons.Quantity;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerId;
import com.apelisser.algashop.ordering.domain.model.product.ProductId;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class ShoppingCart extends AbstractEventSourceEntity implements AggregateRoot<ShoppingCartId> {

    private ShoppingCartId id;
    private CustomerId customerId;
    private Money totalAmount;
    private Quantity totalItems;
    private OffsetDateTime createdAt;
    private Set<ShoppingCartItem> items;

    private Long version;

    @Builder(builderClassName = "ExistingShoppingCartBuilder", builderMethodName = "existing")
    public ShoppingCart(ShoppingCartId id, CustomerId customerId, Money totalAmount, Quantity totalItems,
            OffsetDateTime createdAt, Set<ShoppingCartItem> items) {
        this.setId(id);
        this.setCustomerId(customerId);
        this.setTotalAmount(totalAmount);
        this.setTotalItems(totalItems);
        this.setCreatedAt(createdAt);
        this.setItems(items);
    }

    public static ShoppingCart startShopping(CustomerId customerId) {
        ShoppingCart shoppingCart =  new ShoppingCart(
            new ShoppingCartId(),
            customerId,
            Money.ZERO,
            Quantity.ZERO,
            OffsetDateTime.now(),
            new HashSet<>()
        );

        shoppingCart.publishDomainEvent(new ShoppingCartCreatedEvent(
            shoppingCart.id(),
            shoppingCart.customerId(),
            shoppingCart.createdAt())
        );

        return shoppingCart;
    }

    public void empty() {
        this.items.clear();
        this.recalculateTotals();

        this.publishDomainEvent(new ShoppingCartEmptiedEvent(
            this.id(),
            this.customerId(),
            OffsetDateTime.now())
        );
    }

    public void removeItem(ShoppingCartItemId shoppingCartItemId) {
        ShoppingCartItem shoppingCartItem = this.findItem(shoppingCartItemId);
        this.items.remove(shoppingCartItem);
        this.recalculateTotals();

        this.publishDomainEvent(new ShoppingCartItemRemovedEvent(
            this.id(),
            this.customerId(),
            shoppingCartItem.productId(),
            OffsetDateTime.now())
        );
    }

    public void addItem(Product product, Quantity quantity) {
        Objects.requireNonNull(product);
        Objects.requireNonNull(quantity);

        product.checkOutOfStock();

        Optional<ShoppingCartItem> shoppingCartItem = this.searchItemByProductId(product.id());
        if (shoppingCartItem.isPresent()) {
            this.updateItemByIncreasingQuantity(shoppingCartItem.get(), product, quantity);
        } else {
            ShoppingCartItem newItem = ShoppingCartItem.brandNew()
                .shoppingCartId(this.id())
                .productId(product.id())
                .productName(product.name())
                .price(product.price())
                .quantity(quantity)
                .available(product.inStock())
                .build();

            this.insertItem(newItem);
        }
        this.recalculateTotals();

        this.publishDomainEvent(new ShoppingCartItemAddedEvent(
            this.id(),
            this.customerId(),
            product.id(),
            OffsetDateTime.now())
        );
    }

    public void refreshItem(Product product) {
        Objects.requireNonNull(product);

        ShoppingCartItem shoppingCartItem = this.findItem(product.id());
        shoppingCartItem.refresh(product);
        this.recalculateTotals();
    }

    public ShoppingCartItem findItem(ShoppingCartItemId shoppingCartItemId) {
        return this.items.stream()
            .filter(item -> item.id().equals(shoppingCartItemId))
            .findFirst()
            .orElseThrow(() -> new ShoppingCartDoesNotContainItemException(this.id(), shoppingCartItemId));
    }

    public ShoppingCartItem findItem(ProductId productId) {
        return this.items.stream()
            .filter(item -> item.productId().equals(productId))
            .findFirst()
            .orElseThrow(() -> new ShoppingCartDoesNotContainItemException(this.id(), productId));
    }

    public void changeItemQuantity(ShoppingCartItemId shoppingCartItemId, Quantity quantity) {
        ShoppingCartItem shoppingCartItem = this.findItem(shoppingCartItemId);
        shoppingCartItem.changeQuantity(quantity);
        this.recalculateTotals();
    }

    public boolean containsUnavailableItems() {
        return this.items.stream().anyMatch(item -> !item.isAvailable());
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    public Set<ShoppingCartItem> items() {
        return Collections.unmodifiableSet(items);
    }

    public ShoppingCartId id() {
        return this.id;
    }

    public CustomerId customerId() {
        return this.customerId;
    }

    public Money totalAmount() {
        return this.totalAmount;
    }

    public Quantity totalItems() {
        return this.totalItems;
    }

    public OffsetDateTime createdAt() {
        return this.createdAt;
    }

    public Long version() {
        return version;
    }

    private void recalculateTotals() {
        if (this.isEmpty()) {
            this.setTotalAmount(Money.ZERO);
            this.setTotalItems(Quantity.ZERO);
            return;
        }

        BigDecimal newTotalAmount = this.items.stream()
            .map(item -> item.totalAmount().value())
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer newTotalItems = this.items.stream()
            .map(item -> item.quantity().value())
            .reduce(0, Integer::sum);

        this.setTotalAmount(new Money(newTotalAmount));
        this.setTotalItems(new Quantity(newTotalItems));
    }

    private Optional<ShoppingCartItem> searchItemByProductId(ProductId productId) {
        return this.items.stream()
            .filter(item -> item.productId().equals(productId))
            .findFirst();
    }

    private void updateItemByIncreasingQuantity(ShoppingCartItem shoppingCartItem, Product product, Quantity quantity) {
        shoppingCartItem.refresh(product);
        shoppingCartItem.changeQuantity(shoppingCartItem.quantity().add(quantity));
    }

    private void insertItem(ShoppingCartItem item) {
        this.items.add(item);
    }

    private void setId(ShoppingCartId id) {
        Objects.requireNonNull(id);
        this.id = id;
    }

    private void setCustomerId(CustomerId customerId) {
        Objects.requireNonNull(customerId);
        this.customerId = customerId;
    }

    private void setTotalAmount(Money totalAmount) {
        Objects.requireNonNull(totalAmount);
        this.totalAmount = totalAmount;
    }

    private void setTotalItems(Quantity totalItems) {
        Objects.requireNonNull(totalItems);
        this.totalItems = totalItems;
    }

    private void setCreatedAt(OffsetDateTime createdAt) {
        Objects.requireNonNull(createdAt);
        this.createdAt = createdAt;
    }

    private void setItems(Set<ShoppingCartItem> items) {
        Objects.requireNonNull(items);
        this.items = items;
    }

    private void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ShoppingCart that = (ShoppingCart) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
