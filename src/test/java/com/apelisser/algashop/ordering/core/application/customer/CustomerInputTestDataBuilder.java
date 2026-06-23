package com.apelisser.algashop.ordering.core.application.customer;

import com.apelisser.algashop.ordering.core.ports.in.commons.AddressData;
import com.apelisser.algashop.ordering.core.ports.in.customer.CustomerInput;

import java.time.LocalDate;

public class CustomerInputTestDataBuilder {

    public static CustomerInput.CustomerInputBuilder aCustomer() {
        return CustomerInput.builder()
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
                .build());
    }

}
