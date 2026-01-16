package com.apelisser.algashop.ordering.application.service;

import com.apelisser.algashop.ordering.application.model.AddressData;
import com.apelisser.algashop.ordering.application.model.CustomerInput;
import com.apelisser.algashop.ordering.application.model.CustomerOutput;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.UUID;

@SpringBootTest
class CustomerManagementApplicationServiceIT {

    @Autowired
    CustomerManagementApplicationService customerManagementApplicationService;

    @Test
    void shouldRegister() {
        CustomerInput input = CustomerInput.builder()
            .firstName("John")
            .lastName("Smith")
            .email("john.smith@example.com")
            .phone("478-256-2604")
            .document("255-08-0578")
            .birthDate(LocalDate.of(1991, 7, 5))
            .promotionNotificationsAllowed(true)
            .address(AddressData.builder()
                .street("Bourbon Street")
                .number("1200")
                .complement("Apt. 1200")
                .neighborhood("North Ville")
                .city("Yostfort")
                .state("Souht Carolina")
                .zipCode("70283")
                .build())
            .build();

        UUID customerId = customerManagementApplicationService.create(input);
        Assertions.assertThat(customerId).isNotNull();

        CustomerOutput customerOutput = customerManagementApplicationService.findById(customerId);

        Assertions.assertThat(customerOutput).isNotNull();
        Assertions.assertThat(customerOutput.getId()).isEqualTo(customerId);
        Assertions.assertThat(customerOutput.getFirstName()).isEqualTo("John");
        Assertions.assertThat(customerOutput.getLastName()).isEqualTo("Smith");
        Assertions.assertThat(customerOutput.getEmail()).isEqualTo("john.smith@example.com");
        Assertions.assertThat(customerOutput.getBirthDate()).isEqualTo(LocalDate.of(1991, 7, 5));
        Assertions.assertThat(customerOutput.getRegisteredAt()).isNotNull();
    }

}