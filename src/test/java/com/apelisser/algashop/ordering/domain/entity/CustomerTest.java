package com.apelisser.algashop.ordering.domain.entity;

import com.apelisser.algashop.ordering.domain.utility.IdGenerator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    void givenInvalidEmail_whenCreateCustomer_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() ->
                new Customer(
                    IdGenerator.generateTimeBasedUUID(),
                    "John Doe",
                    LocalDate.of(1991, 7, 5),
                    "invalidEmail",
                    "478-256-2504",
                    "255-08-0578",
                    false,
                    OffsetDateTime.now()
                )
            );
    }

    @Test
    void givenInvalidEmail_whenTryUpdateCustomerEmail_shouldGenerateException() {
        Customer customer = new Customer(
            IdGenerator.generateTimeBasedUUID(),
            "John Doe",
            LocalDate.of(1991, 7, 5),
            "john.doe@example.com",
            "478-256-2504",
            "255-08-0578",
            false,
            OffsetDateTime.now()
        );

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> customer.changeEmail("invalidEmail"));
    }

    @Test
    void givenUnarchivedCustomer_whenArchive_shouldAnonymize() {
        Customer customer = new Customer(
            IdGenerator.generateTimeBasedUUID(),
            "John Doe",
            LocalDate.of(1991, 7, 5),
            "john.doe@example.com",
            "478-256-2504",
            "255-08-0578",
            false,
            OffsetDateTime.now()
        );

        customer.archive();

        Assertions.assertWith(customer,
            c -> Assertions.assertThat(c.fullName()).isEqualTo("Anonymous"),
            c -> Assertions.assertThat(c.email()).isNotEqualTo("john.doe@example.com"),
            c -> Assertions.assertThat(c.phone()).isEqualTo("000-000-0000"),
            c -> Assertions.assertThat(c.document()).isEqualTo("000-00-0000"),
            c -> Assertions.assertThat(c.birthDate()).isNull()
        );
    }

}