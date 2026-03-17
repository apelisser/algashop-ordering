package com.apelisser.algashop.ordering.infrastructure.product.client.http;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ProductResponse {

    private UUID id;
    private String name;
    private BigDecimal salePrice;
    private Boolean inStock;

    public ProductResponse() {
    }

    public ProductResponse(UUID id, String name, BigDecimal salePrice, Boolean inStock) {
        this.id = id;
        this.name = name;
        this.salePrice = salePrice;
        this.inStock = inStock;
    }

}
