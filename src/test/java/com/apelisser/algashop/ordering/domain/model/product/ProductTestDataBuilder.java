package com.apelisser.algashop.ordering.domain.model.product;

import com.apelisser.algashop.ordering.domain.model.commons.Money;

public final class ProductTestDataBuilder {

    public static ProductId DEFAULT_PRODUCT_ID = new ProductId();
    public static ProductId UNAVAILABLE_PRODUCT_ID = new ProductId();
    public static ProductId RAM_MEMORY_PRODUCT_ID = new ProductId();
    public static ProductId MOUSE_PAD_PRODUCT_ID = new ProductId();

    private ProductTestDataBuilder() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Product.ProductBuilder aProduct() {
        return Product.builder()
            .id(DEFAULT_PRODUCT_ID)
            .name(new ProductName("Notebook X11"))
            .price(new Money("3000"))
            .inStock(true);
    }

    public static Product.ProductBuilder aProductUnavailable() {
        return Product.builder()
            .id(UNAVAILABLE_PRODUCT_ID)
            .name(new ProductName("Desktop X1000"))
            .price(new Money("5000"))
            .inStock(false);
    }

    public static Product.ProductBuilder aProductAltRamMemory() {
        return Product.builder()
            .id(RAM_MEMORY_PRODUCT_ID)
            .name(new ProductName("16GB RAM Memory"))
            .price(new Money("600"))
            .inStock(true);
    }

    public static Product.ProductBuilder aProductAltMousePad() {
        return Product.builder()
            .id(MOUSE_PAD_PRODUCT_ID)
            .name(new ProductName("Mouse Pad"))
            .price(new Money("100"))
            .inStock(true);
    }

}
