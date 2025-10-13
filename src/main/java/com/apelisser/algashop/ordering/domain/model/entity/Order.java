package com.apelisser.algashop.ordering.domain.model.entity;

import com.apelisser.algashop.ordering.domain.model.exception.OrderCannotBeEditedException;
import com.apelisser.algashop.ordering.domain.model.exception.OrderCannotBePlacedException;
import com.apelisser.algashop.ordering.domain.model.exception.OrderDoesNotContainOrderItemException;
import com.apelisser.algashop.ordering.domain.model.exception.OrderInvalidShippingDeliveryDateException;
import com.apelisser.algashop.ordering.domain.model.exception.OrderStatusCannotBeChangedException;
import com.apelisser.algashop.ordering.domain.model.valueobject.Billing;
import com.apelisser.algashop.ordering.domain.model.valueobject.Money;
import com.apelisser.algashop.ordering.domain.model.valueobject.Product;
import com.apelisser.algashop.ordering.domain.model.valueobject.Quantity;
import com.apelisser.algashop.ordering.domain.model.valueobject.Shipping;
import com.apelisser.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.apelisser.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.apelisser.algashop.ordering.domain.model.valueobject.id.OrderItemId;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Order implements AggregateRoot<OrderId> {

    private OrderId id;
    private CustomerId customerId;
    private Money totalAmount;
    private Quantity totalItems;
    private OffsetDateTime placedAt;
    private OffsetDateTime paidAt;
    private OffsetDateTime canceledAt;
    private OffsetDateTime readyAt;
    private Billing billing;
    private Shipping shipping;
    private OrderStatus status;
    private PaymentMethod paymentMethod;
    private Set<OrderItem> items;
    private Long version;

    @Builder(builderClassName = "ExistingOrderBuilder", builderMethodName = "existing")
    public Order(OrderId id, CustomerId customerId, Money totalAmount, Quantity totalItems, OffsetDateTime placedAt,
            OffsetDateTime paidAt, OffsetDateTime canceledAt, OffsetDateTime readyAt, Billing billing,
            Shipping shipping, OrderStatus status, PaymentMethod paymentMethod, Long version, Set<OrderItem> items) {
        this.setId(id);
        this.setCustomerId(customerId);
        this.setTotalAmount(totalAmount);
        this.setTotalItems(totalItems);
        this.setPlacedAt(placedAt);
        this.setPaidAt(paidAt);
        this.setCanceledAt(canceledAt);
        this.setReadyAt(readyAt);
        this.setBilling(billing);
        this.setShipping(shipping);
        this.setStatus(status);
        this.setPaymentMethod(paymentMethod);
        this.setVersion(version);
        this.setItems(items);
    }

    public static Order draft(CustomerId customerId) {
        return new Order(
            new OrderId(),
            customerId,
            Money.ZERO,
            Quantity.ZERO,
            null,
            null,
            null,
            null,
            null,
            null,
            OrderStatus.DRAFT,
            null,
            null,
            new HashSet<>()
        );
    }

    public void addItem(Product product, Quantity quantity) {
        this.verifyIfChangeable();

        Objects.requireNonNull(product);
        Objects.requireNonNull(quantity);

        product.checkOutOfStock();

        OrderItem item = OrderItem.brandNew()
            .orderId(this.id())
            .product(product)
            .quantity(quantity)
            .build();

        if (this.items == null) {
            this.items = new HashSet<>();
        }

        this.items.add(item);

        this.recalculateTotals();
    }

    public void removeItem(OrderItemId orderItemId) {
        this.verifyIfChangeable();
        Objects.requireNonNull(orderItemId);

        OrderItem orderItem = this.findOrderItem(orderItemId);
        this.items.remove(orderItem);
        this.recalculateTotals();
    }

    public void place() {
        this.verifyIfCanChangeToPlaced();
        this.changeStatus(OrderStatus.PLACED);
        this.setPlacedAt(OffsetDateTime.now());
    }

    public void markAsPaid() {
        this.changeStatus(OrderStatus.PAID);
        this.setPaidAt(OffsetDateTime.now());
    }

    public void markAsReady() {
        this.changeStatus(OrderStatus.READY);
        this.setReadyAt(OffsetDateTime.now());
    }

    public void cancel() {
        this.changeStatus(OrderStatus.CANCELED);
        this.setCanceledAt(OffsetDateTime.now());
    }

    public void changePaymentMethod(PaymentMethod paymentMethod) {
        this.verifyIfChangeable();
        Objects.requireNonNull(paymentMethod);
        this.setPaymentMethod(paymentMethod);
    }

    public void changeBilling(Billing billing) {
        this.verifyIfChangeable();
        Objects.requireNonNull(billing);
        this.setBilling(billing);
    }

    public void changeShipping(Shipping shipping) {
        this.verifyIfChangeable();
        Objects.requireNonNull(shipping);

        if (shipping.expectedDate().isBefore(LocalDate.now())) {
            throw new OrderInvalidShippingDeliveryDateException(this.id());
        }

        this.setShipping(shipping);
    }

    public void changeItemQuantity(OrderItemId orderItemId, Quantity quantity) {
        this.verifyIfChangeable();
        Objects.requireNonNull(orderItemId);
        Objects.requireNonNull(quantity);

        OrderItem item = this.findOrderItem(orderItemId);
        item.changeQuantity(quantity);

        this.recalculateTotals();
    }

    public boolean isDraft() {
        return this.status() == OrderStatus.DRAFT;
    }

    public boolean isPlaced() {
        return this.status() == OrderStatus.PLACED;
    }

    public boolean isPaid() {
        return this.status() == OrderStatus.PAID;
    }

    public boolean isCanceled() {
        return this.status() == OrderStatus.CANCELED;
    }

    public OrderId id() {
        return id;
    }

    public CustomerId customerId() {
        return customerId;
    }

    public Money totalAmount() {
        return totalAmount;
    }

    public Quantity totalItems() {
        return totalItems;
    }

    public OffsetDateTime placedAt() {
        return placedAt;
    }

    public OffsetDateTime paidAt() {
        return paidAt;
    }

    public OffsetDateTime canceledAt() {
        return canceledAt;
    }

    public OffsetDateTime readyAt() {
        return readyAt;
    }

    public Billing billing() {
        return billing;
    }

    public Shipping shipping() {
        return shipping;
    }

    public OrderStatus status() {
        return status;
    }

    public PaymentMethod paymentMethod() {
        return paymentMethod;
    }

    public Long version() {
        return version;
    }

    public Set<OrderItem> items() {
        return Collections.unmodifiableSet(items);
    }

    private void recalculateTotals() {
        BigDecimal totalItemsAmount = this.items.stream()
            .map(item -> item.totalAmount().value())
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer totalItemsQuantity = this.items.stream()
            .map(item -> item.quantity().value())
            .reduce(0, Integer::sum);

        BigDecimal shippingCost = this.shipping() == null
            ? BigDecimal.ZERO
            : this.shipping.cost().value();

        BigDecimal totalAmount = totalItemsAmount.add(shippingCost);

        this.setTotalAmount(new Money(totalAmount));
        this.setTotalItems(new Quantity(totalItemsQuantity));
    }

    private void changeStatus(OrderStatus newStatus) {
        Objects.requireNonNull(newStatus);
        if (this.status.cannotChangeTo(newStatus)) {
            throw new OrderStatusCannotBeChangedException(this.id(), this.status(), newStatus);
        }
        this.setStatus(newStatus);
    }

    private void verifyIfCanChangeToPlaced() {
        if (this.shipping() == null) {
            throw OrderCannotBePlacedException.noShippingInfo(this.id());
        }
        if (this.billing() == null) {
            throw OrderCannotBePlacedException.noBillingInfo(this.id());
        }
        if (this.paymentMethod() == null) {
            throw OrderCannotBePlacedException.noPaymentMethod(this.id());
        }
        if (this.items() == null || items.isEmpty()) {
            throw OrderCannotBePlacedException.noItems(this.id());
        }
    }

    private void verifyIfChangeable() {
        if (!this.isDraft()) {
            throw new OrderCannotBeEditedException(this.id(), this.status());
        }
    }

    private OrderItem findOrderItem(OrderItemId orderItemId) {
        Objects.requireNonNull(orderItemId);
        return this.items().stream()
            .filter(item -> item.id().equals(orderItemId))
            .findFirst()
            .orElseThrow(() -> new OrderDoesNotContainOrderItemException(this.id(), orderItemId));
    }

    private void setId(OrderId id) {
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

    private void setPlacedAt(OffsetDateTime placedAt) {
        this.placedAt = placedAt;
    }

    private void setPaidAt(OffsetDateTime paidAt) {
        this.paidAt = paidAt;
    }

    private void setCanceledAt(OffsetDateTime canceledAt) {
        this.canceledAt = canceledAt;
    }

    private void setReadyAt(OffsetDateTime readyAt) {
        this.readyAt = readyAt;
    }

    private void setBilling(Billing billing) {
        this.billing = billing;
    }

    private void setShipping(Shipping shipping) {
        this.shipping = shipping;
    }

    private void setStatus(OrderStatus status) {
        Objects.requireNonNull(status);
        this.status = status;
    }

    private void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    private void setVersion(Long version) {
        this.version = version;
    }

    private void setItems(Set<OrderItem> items) {
        Objects.requireNonNull(items);
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
