package com.apelisser.algashop.ordering.domain.model.valueobject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class BirthDateTest {

    @Test
    void shouldGenerate() {
        BirthDate birthDate = new BirthDate(LocalDate.of(1991, 7, 5));
        Assertions.assertWith(birthDate,
            bd -> {
                Assertions.assertThat(bd.value().getYear()).isEqualTo(1991);
                Assertions.assertThat(bd.value().getMonthValue()).isEqualTo(7);
                Assertions.assertThat(bd.value().getDayOfMonth()).isEqualTo(5);
            });
    }

    @Test
    void shouldCalculateAge() {
        int yearOfBirth = 1991;
        int expectedAge = LocalDate.now().getYear() - yearOfBirth;

        BirthDate birthDate = new BirthDate(LocalDate.of(yearOfBirth, 7, 5));

        Assertions.assertThat(birthDate.age()).isEqualTo(expectedAge);
    }

    @Test
    void shouldNotGenerate() {
        Assertions.assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> new BirthDate(null));

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new BirthDate(LocalDate.now().plusDays(1)));
    }

}
