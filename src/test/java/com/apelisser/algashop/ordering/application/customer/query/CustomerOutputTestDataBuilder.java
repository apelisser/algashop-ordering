package com.apelisser.algashop.ordering.application.customer.query;

import com.apelisser.algashop.ordering.application.commons.AddressData;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class CustomerOutputTestDataBuilder {

    public static CustomerOutput.CustomerOutputBuilder existing() {
        return CustomerOutput.builder()
            .id(UUID.randomUUID())
            .registeredAt(OffsetDateTime.now())
            .phone("(11) 99999-9999")
            .email("john.doe@example.com")
            .firstName("John")
            .lastName("Doe")
            .birthDate(LocalDate.of(1991, 7, 5))
            .document("123.456.789-00")
            .promotionNotificationsAllowed(false)
            .loyaltyPoints(0)
            .archived(false)
            .address(AddressData.builder()
                .street("Main Street")
                .number("123")
                .complement("Apartment 1")
                .neighborhood("Center")
                .city("New York")
                .state("New York")
                .zipCode("12345")
                .build());
    }

}
