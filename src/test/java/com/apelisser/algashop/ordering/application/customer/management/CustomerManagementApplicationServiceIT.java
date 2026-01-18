package com.apelisser.algashop.ordering.application.customer.management;

import com.apelisser.algashop.ordering.domain.model.customer.Customers;
import com.apelisser.algashop.ordering.domain.model.product.ProductCatalogService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@SpringBootTest
@Transactional
class CustomerManagementApplicationServiceIT {

    @MockitoBean
    ProductCatalogService productCatalogService;

    @Autowired
    CustomerManagementApplicationService customerManagementApplicationService;

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

}