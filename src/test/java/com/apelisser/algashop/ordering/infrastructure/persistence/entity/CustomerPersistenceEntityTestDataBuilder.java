package com.apelisser.algashop.ordering.infrastructure.persistence.entity;

import com.apelisser.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.apelisser.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddableTestDataBuilder;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class CustomerPersistenceEntityTestDataBuilder {

    private CustomerPersistenceEntityTestDataBuilder() {
    }

    public static CustomerPersistenceEntity.CustomerPersistenceEntityBuilder existingCustomer() {
        return CustomerPersistenceEntity.builder()
            .id(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID.value())
            .firstName("John")
            .lastName("Doe")
            .birthDate(LocalDate.parse("1990-01-23"))
            .email("john.doe@example.com")
            .phone("123456789")
            .document("123456789")
            .promotionNotificationsAllowed(true)
            .archived(false)
            .registeredAt(OffsetDateTime.now())
            .archivedAt(null)
            .loyaltyPoints(1)
            .address(AddressEmbeddableTestDataBuilder.existingAddress().build());
    }

}
