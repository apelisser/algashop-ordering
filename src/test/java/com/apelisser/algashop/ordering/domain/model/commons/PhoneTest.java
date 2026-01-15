package com.apelisser.algashop.ordering.domain.model.commons;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class PhoneTest {

    @Test
    void shouldGenerate() {
        Phone phone = new Phone("478-256-2504");
        Assertions.assertThat(phone.value()).isEqualTo("478-256-2504");
    }

    @Test
    void shouldNotGenerate() {
        Assertions.assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> new Phone(null));

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Phone(""));
    }

}
