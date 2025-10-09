package com.apelisser.algashop.ordering.domain.model.valueobject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class DocumentTest {

    @Test
    void shouldGenerate() {
        Document document = new Document("12345678901");
        Assertions.assertThat(document.value()).isEqualTo("12345678901");
    }

    @Test
    void shouldNotGenerate() {
        Assertions.assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> new Document(null));

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Document(""));
    }

}
