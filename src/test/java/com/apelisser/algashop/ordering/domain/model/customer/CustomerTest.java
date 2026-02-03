package com.apelisser.algashop.ordering.domain.model.customer;

import com.apelisser.algashop.ordering.domain.model.commons.Email;
import com.apelisser.algashop.ordering.domain.model.commons.FullName;
import com.apelisser.algashop.ordering.domain.model.commons.Phone;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CustomerTest {

    @Test
    void givenInvalidEmail_whenCreateCustomer_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() ->
                CustomerTestDataBuilder.brandNewCustomer()
                    .email(new Email("invalidEmail"))
                    .build()
            );
    }

    @Test
    void givenInvalidEmail_whenTryUpdateCustomerEmail_shouldGenerateException() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> customer.changeEmail(new Email("invalidEmail")));
    }

    @Test
    void givenUnarchivedCustomer_whenArchive_shouldAnonymize() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();

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
        Customer customer = CustomerTestDataBuilder.existingAnonymizedCustomer().build();

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
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();

        customer.addLoyaltyPoints(new LoyaltyPoints(10));
        customer.addLoyaltyPoints(new LoyaltyPoints(20));

        Assertions.assertThat(customer.loyaltyPoints()).isEqualTo(new LoyaltyPoints(30));
    }

    @Test
    void givenBrandNewCustomer_whenAddInvalidLoyaltyPoints_shouldGenerateException() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();

        Assertions.assertThatNoException()
            .isThrownBy(() -> customer.addLoyaltyPoints(LoyaltyPoints.ZERO));

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> customer.addLoyaltyPoints(new LoyaltyPoints(-10)));
    }

    @Test
    void givenValidData_whenCreateBrandNewCustomer_shouldGenerateCustomerRegisteredEvent() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        CustomerRegisteredEvent event = new CustomerRegisteredEvent(
            customer.id(),
            customer.registeredAt(),
            customer.fullName(),
            customer.email());

        Assertions.assertThat(customer.domainEvents()).contains(event);
    }

    @Test
    void givenUnarchivedCustomer_whenArchive_shouldGenerateCustomerArchivedEvent() {
        Customer customer = CustomerTestDataBuilder.existingCustomer()
            .archived(false)
            .archivedAt(null)
            .build();

        customer.archive();

        CustomerArchivedEvent event = new CustomerArchivedEvent(customer.id(), customer.archivedAt());
        Assertions.assertThat(customer.domainEvents()).contains(event);
    }

}
