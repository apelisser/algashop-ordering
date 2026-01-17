package com.apelisser.algashop.ordering.application.customer.management;

import com.apelisser.algashop.ordering.application.commons.AddressData;

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
