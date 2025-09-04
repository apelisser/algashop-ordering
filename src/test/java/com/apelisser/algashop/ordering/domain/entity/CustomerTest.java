package com.apelisser.algashop.ordering.domain.entity;

import com.apelisser.algashop.ordering.domain.exception.CustomerArchivedException;
import com.apelisser.algashop.ordering.domain.valueobject.BirthDate;
import com.apelisser.algashop.ordering.domain.valueobject.CustomerId;
import com.apelisser.algashop.ordering.domain.valueobject.Document;
import com.apelisser.algashop.ordering.domain.valueobject.Email;
import com.apelisser.algashop.ordering.domain.valueobject.FullName;
import com.apelisser.algashop.ordering.domain.valueobject.LoyaltyPoints;
import com.apelisser.algashop.ordering.domain.valueobject.Phone;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

class CustomerTest {

    @Test
    void givenInvalidEmail_whenCreateCustomer_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() ->
                new Customer(
                    new CustomerId(),
                    new FullName("John", "Doe"),
                    new BirthDate(LocalDate.of(1991, 7, 5)),
                    new Email("invalidEmail"),
                    new Phone("478-256-2504"),
                    new Document("255-08-0578"),
                    false,
                    OffsetDateTime.now()
                )
            );
    }

    @Test
    void givenInvalidEmail_whenTryUpdateCustomerEmail_shouldGenerateException() {
        Customer customer = new Customer(
            new CustomerId(),
            new FullName("John", "Doe"),
            new BirthDate(LocalDate.of(1991, 7, 5)),
            new Email("john.doe@example.com"),
            new Phone("478-256-2504"),
            new Document("255-08-0578"),
            false,
            OffsetDateTime.now()
        );

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> customer.changeEmail(new Email("invalidEmail")));
    }

    @Test
    void givenUnarchivedCustomer_whenArchive_shouldAnonymize() {
        Customer customer = new Customer(
            new CustomerId(),
            new FullName("John", "Doe"),
            new BirthDate(LocalDate.of(1991, 7, 5)),
            new Email("john.doe@example.com"),
            new Phone("478-256-2504"),
            new Document("255-08-0578"),
            false,
            OffsetDateTime.now()
        );

        customer.archive();

        Assertions.assertWith(customer,
            c -> Assertions.assertThat(c.fullName()).isEqualTo(new FullName("Anonymous", "Anonymous")),
            c -> Assertions.assertThat(c.email().value()).isNotEqualTo("john.doe@example.com"),
            c -> Assertions.assertThat(c.phone().value()).isEqualTo("000-000-0000"),
            c -> Assertions.assertThat(c.document().value()).isEqualTo("000-00-0000"),
            c -> Assertions.assertThat(c.birthDate()).isNull()
        );
    }

    @Test
    void givenArchivedCustomer_whenTryToUpdate_shouldGenerateException() {
        Customer customer = new Customer(
            new CustomerId(),
            new FullName("Anonymous", "Anonymous"),
            null,
            new Email(UUID.randomUUID() + "@anonymous.com"),
            new Phone("000-000-0000"),
            new Document("000-00-0000"),
            false,
            true,
            OffsetDateTime.now(),
            OffsetDateTime.now(),
            new LoyaltyPoints(10)
        );

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
            .isThrownBy(customer::archive);

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
            .isThrownBy(() -> customer.changeEmail(new Email("email@example.com")));

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
            .isThrownBy(() -> customer.changePhone(new Phone("478-256-2504")));

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
            .isThrownBy(customer::enablePromotionNotifications);

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
            .isThrownBy(customer::disablePromotionNotifications);
    }

    @Test
    void givenBrandNewCustomer_whenAddLoyaltyPoints_shouldSumPoints() {
        Customer customer = new Customer(
            new CustomerId(),
            new FullName("John", "Doe"),
            new BirthDate(LocalDate.of(1991, 7, 5)),
            new Email("john.doe@example.com"),
            new Phone("478-256-2504"),
            new Document("255-08-0578"),
            false,
            OffsetDateTime.now()
        );

        customer.addLoyaltyPoints(new LoyaltyPoints(10));
        customer.addLoyaltyPoints(new LoyaltyPoints(20));

        Assertions.assertThat(customer.loyaltyPoints()).isEqualTo(new LoyaltyPoints(30));
    }

    @Test
    void givenBrandNewCustomer_whenAddInvalidLoyaltyPoints_shouldGenerateException() {
        Customer customer = new Customer(
            new CustomerId(),
            new FullName("John", "Doe"),
            new BirthDate(LocalDate.of(1991, 7, 5)),
            new Email("john.doe@example.com"),
            new Phone("478-256-2504"),
            new Document("255-08-0578"),
            false,
            OffsetDateTime.now()
        );

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> customer.addLoyaltyPoints(LoyaltyPoints.ZERO));

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> customer.addLoyaltyPoints(new LoyaltyPoints(-10)));
    }

}
