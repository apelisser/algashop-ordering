package com.apelisser.algashop.ordering.application.customer.management;

import com.apelisser.algashop.ordering.application.customer.notification.CustomerNotificationService;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerArchivedEvent;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerArchivedException;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerEmailIsInUseException;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerId;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerRegisteredEvent;
import com.apelisser.algashop.ordering.infrastructure.listener.customer.CustomerEventListener;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@SpringBootTest
@Transactional
class CustomerManagementApplicationServiceIT {

    @Autowired
    CustomerManagementApplicationService customerManagementApplicationService;

    @MockitoSpyBean
    CustomerEventListener customerEventListener;

    @MockitoSpyBean
    CustomerNotificationService customerNotificationService;

    @Test
    void shouldRegister() {
        CustomerInput input = CustomerInputTestDataBuilder.aCustomer().build();

        UUID customerId = customerManagementApplicationService.create(input);
        Assertions.assertThat(customerId).isNotNull();

        CustomerOutput customerOutput = customerManagementApplicationService.findById(customerId);

        Assertions.assertThat(customerOutput)
            .extracting(
                CustomerOutput::getId,
                CustomerOutput::getFirstName,
                CustomerOutput::getLastName,
                CustomerOutput::getEmail,
                CustomerOutput::getBirthDate)
            .containsExactly(
                customerId,
                "John",
                "Smith",
                "john.smith@example.com",
                LocalDate.of(1991, 7, 5));
        Assertions.assertThat(customerOutput.getRegisteredAt()).isNotNull();

        Mockito.verify(customerEventListener)
            .listen(Mockito.any(CustomerRegisteredEvent.class));

        Mockito.verify(customerEventListener, Mockito.never())
            .listen(Mockito.any(CustomerArchivedEvent.class));

        Mockito.verify(customerNotificationService)
            .notifyNewRegistration(Mockito.any(UUID.class));
    }

    @Test
    void shouldUpdate() {
        CustomerInput input = CustomerInputTestDataBuilder.aCustomer().build();
        CustomerUpdateInput updateInput = CustomerUpdateInputTestDataBuilder.aCustomerUpdate().build();

        UUID customerId = customerManagementApplicationService.create(input);
        Assertions.assertThat(customerId).isNotNull();

        customerManagementApplicationService.update(customerId, updateInput);

        CustomerOutput customerOutput = customerManagementApplicationService.findById(customerId);

        Assertions.assertThat(customerOutput)
            .extracting(
                CustomerOutput::getId,
                CustomerOutput::getFirstName,
                CustomerOutput::getLastName,
                CustomerOutput::getEmail,
                CustomerOutput::getPhone,
                CustomerOutput::getPromotionNotificationsAllowed,
                CustomerOutput::getBirthDate)
            .containsExactly(
                customerId,
                "Matt",
                "Damon",
                "john.smith@example.com",
                "123-321-1112",
                Boolean.TRUE,
                LocalDate.of(1991, 7, 5));

        Assertions.assertThat(customerOutput.getRegisteredAt()).isNotNull();
    }

    @Test
    void shouldArchive() {
        CustomerInput customerInput = CustomerInputTestDataBuilder.aCustomer().build();
        UUID customerId = customerManagementApplicationService.create(customerInput);
        customerManagementApplicationService.archive(customerId);
        CustomerOutput archivedCustomer = customerManagementApplicationService.findById(customerId);

        Assertions.assertThat(archivedCustomer).isNotNull();
        Assertions.assertThat(archivedCustomer.getArchived()).isTrue();
        Assertions.assertThat(archivedCustomer.getArchivedAt()).isNotNull();
        Assertions.assertThat(archivedCustomer.getFirstName()).isEqualTo("Anonymous");
        Assertions.assertThat(archivedCustomer.getLastName()).isEqualTo("Anonymous");
        Assertions.assertThat(archivedCustomer.getPhone()).isEqualTo("000-000-0000");
        Assertions.assertThat(archivedCustomer.getDocument()).isEqualTo("000-00-0000");
        Assertions.assertThat(archivedCustomer.getEmail()).endsWith("@anonymous.com");
        Assertions.assertThat(archivedCustomer.getPromotionNotificationsAllowed()).isFalse();
        Assertions.assertThat(archivedCustomer.getAddress().getComplement()).isNull();
        Assertions.assertThat(archivedCustomer.getAddress().getNumber()).isEqualTo("Anonymized");
    }

    @Test
    void givenANonExistentCustomer_whenArchive_shouldThrowException() {
        CustomerId customerId = new CustomerId();

        Assertions.assertThatExceptionOfType(CustomerNotFoundException.class)
            .isThrownBy(() -> customerManagementApplicationService.archive(customerId.value()));
    }

    @Test
    void givenAnArchivedCustomer_whenArchive_shouldThrowException() {
        CustomerInput customerInput = CustomerInputTestDataBuilder.aCustomer().build();
        UUID customerId = customerManagementApplicationService.create(customerInput);
        customerManagementApplicationService.archive(customerId);

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
            .isThrownBy(() -> customerManagementApplicationService.archive(customerId));
    }

    @Test
    void shouldChangeEmail() {
        String expectedEmail = "new.email@example.com";

        CustomerInput customerInput = CustomerInputTestDataBuilder.aCustomer().build();
        UUID customerId = customerManagementApplicationService.create(customerInput);
        customerManagementApplicationService.changeEmail(customerId, expectedEmail);

        CustomerOutput customerOutput = customerManagementApplicationService.findById(customerId);

        Assertions.assertThat(customerOutput).isNotNull();
        Assertions.assertThat(customerOutput.getEmail())
            .isNotBlank()
            .isEqualTo(expectedEmail);
    }

    @Test
    void givenANonExistentCustomer_whenChangeEmail_shouldThrowException() {
        Assertions.assertThatExceptionOfType(CustomerNotFoundException.class)
            .isThrownBy(() -> customerManagementApplicationService.changeEmail(
                new CustomerId().value(),
                "new.email@example.com"));
    }

    @Test
    void givenAnArchivedCustomer_whenChangeEmail_shouldThrowException() {
        CustomerInput customerInput = CustomerInputTestDataBuilder.aCustomer().build();
        UUID customerId = customerManagementApplicationService.create(customerInput);
        customerManagementApplicationService.archive(customerId);

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
            .isThrownBy(() -> customerManagementApplicationService.changeEmail(customerId, "new.email@example.com"));
    }

    @Test
    void givenAnInvalidEmail_whenChangeEmail_shouldThrowException() {
        CustomerInput customerInput = CustomerInputTestDataBuilder.aCustomer().build();
        UUID customerId = customerManagementApplicationService.create(customerInput);

        String invalidEmail = "email.com";

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> customerManagementApplicationService.changeEmail(customerId, invalidEmail));
    }

    @Test
    void givenAnAlreadyUsedEmail_whenChangeEmail_shouldThrowException() {
        CustomerInput customer1 = CustomerInputTestDataBuilder.aCustomer()
            .email("customer1@example.com")
            .build();
        UUID customerId1 = customerManagementApplicationService.create(customer1);

        CustomerInput customer2 = CustomerInputTestDataBuilder.aCustomer()
            .email("customer2@example.com")
            .build();
        customerManagementApplicationService.create(customer2);

        Assertions.assertThatExceptionOfType(CustomerEmailIsInUseException.class)
            .isThrownBy(() -> customerManagementApplicationService.changeEmail(customerId1, customer2.getEmail()));
    }

}