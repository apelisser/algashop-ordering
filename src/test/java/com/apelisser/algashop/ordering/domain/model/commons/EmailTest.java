package com.apelisser.algashop.ordering.domain.model.commons;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class EmailTest {

    @Test
    void shouldGenerate() {
        Email email = new Email("john.doe@example.com");
        Assertions.assertThat(email.value()).isEqualTo("john.doe@example.com");
    }

    @Test
    void shouldNotGenerate() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Email(null));

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Email(""));

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Email("invalidEmail"));
    }

}
