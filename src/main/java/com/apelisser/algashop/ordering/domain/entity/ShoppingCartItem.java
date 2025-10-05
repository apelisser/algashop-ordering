package com.apelisser.algashop.ordering.domain.entity;

import com.apelisser.algashop.ordering.domain.exception.ShoppingCartItemIncompatibleProductException;
import com.apelisser.algashop.ordering.domain.valueobject.Money;
import com.apelisser.algashop.ordering.domain.valueobject.Product;
import com.apelisser.algashop.ordering.domain.valueobject.ProductName;
import com.apelisser.algashop.ordering.domain.valueobject.Quantity;
import com.apelisser.algashop.ordering.domain.valueobject.id.ProductId;
import com.apelisser.algashop.ordering.domain.valueobject.id.ShoppingCartId;
import com.apelisser.algashop.ordering.domain.valueobject.id.ShoppingCartItemId;
import lombok.Builder;

import java.util.Objects;

public class ShoppingCartItem {

    private ShoppingCartItemId id;
    private ShoppingCartId shoppingCartId;
    private ProductId productId;
    private ProductName name;
    private Money price;
    private Quantity quantity;
    private Money totalAmount;
    private Boolean available;

    @Builder(builderClassName = "ExistingShoppingCartItemBuilder", builderMethodName = "existing")
    public ShoppingCartItem(ShoppingCartItemId id, ShoppingCartId shoppingCartId, ProductId productId,
            ProductName name, Money price, Quantity quantity, Money totalAmount, Boolean available) {
        this.setId(id);
        this.setShoppingCartId(shoppingCartId);
        this.setProductId(productId);
        this.setName(name);
        this.setPrice(price);
        this.setQuantity(quantity);
        this.setTotalAmount(totalAmount);
        this.setAvailable(available);
    }

    @Builder(builderClassName = "BrandShoppingCartItemBuilder", builderMethodName = "brandNew")
    private static ShoppingCartItem createBrandNew(ShoppingCartId shoppingCartId, ProductId productId,
            ProductName name, Money price, Quantity quantity, Boolean available) {
        ShoppingCartItem shoppingCartItem = new ShoppingCartItem(
            new ShoppingCartItemId(),
            shoppingCartId,
            productId,
            name,
            price,
            quantity,
            Money.ZERO,
            available
        );

        shoppingCartItem.recalculateTotals();

        return shoppingCartItem;
    }

    void refresh(Product product) {
        Objects.requireNonNull(product);

        if (!this.productId().equals(product.id())) {
            throw new ShoppingCartItemIncompatibleProductException(this.productId(), product.id());
        }

        this.setProductId(product.id());
        this.setName(product.name());
        this.setPrice(product.price());
        this.setAvailable(product.inStock());

        this.recalculateTotals();
    }

    void changeQuantity(Quantity quantity) {
        Objects.requireNonNull(quantity);

        if (quantity.compareTo(Quantity.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        this.setQuantity(quantity);
        this.recalculateTotals();
    }

    public ShoppingCartItemId id() {
        return this.id;
    }

    public ShoppingCartId shoppingCartId() {
        return this.shoppingCartId;
    }

    public ProductId productId() {
        return this.productId;
    }

    public ProductName name() {
        return this.name;
    }

    public Money price() {
        return this.price;
    }

    public Quantity quantity() {
        return this.quantity;
    }

    public Money totalAmount() {
        return this.totalAmount;
    }

    public boolean isAvailable() {
        return this.available;
    }

    private void recalculateTotals() {
        this.setTotalAmount(this.price().multiply(this.quantity()));
    }

    private void setId(ShoppingCartItemId id) {
        Objects.requireNonNull(id);
        this.id = id;
    }

    private void setShoppingCartId(ShoppingCartId shoppingCartId) {
        Objects.requireNonNull(shoppingCartId);
        this.shoppingCartId = shoppingCartId;
    }

    private void setProductId(ProductId productId) {
        Objects.requireNonNull(productId);
        this.productId = productId;
    }

    private void setName(ProductName name) {
        Objects.requireNonNull(name);
        this.name = name;
    }

    private void setPrice(Money price) {
        Objects.requireNonNull(price);
        this.price = price;
    }

    private void setQuantity(Quantity quantity) {
        Objects.requireNonNull(quantity);
        this.quantity = quantity;
    }

    private void setTotalAmount(Money totalAmount) {
        Objects.requireNonNull(totalAmount);
        this.totalAmount = totalAmount;
    }

    private void setAvailable(Boolean available) {
        Objects.requireNonNull(available);
        this.available = available;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ShoppingCartItem that = (ShoppingCartItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
