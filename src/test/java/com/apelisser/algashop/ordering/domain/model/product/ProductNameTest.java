package com.apelisser.algashop.ordering.domain.model.product;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ProductNameTest {


    @Test
    void shouldGenerate() {
        Assertions.assertThatCode(() -> new ProductName("Product 01")).doesNotThrowAnyException();
    }

    @Test
    void shouldNotGenerate() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new ProductName(null));

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new ProductName(" "));
    }

}
