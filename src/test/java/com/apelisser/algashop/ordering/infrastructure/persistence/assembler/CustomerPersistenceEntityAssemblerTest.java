package com.apelisser.algashop.ordering.infrastructure.persistence.assembler;

import com.apelisser.algashop.ordering.domain.model.customer.Customer;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.commons.Address;
import com.apelisser.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import com.apelisser.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityAssembler;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CustomerPersistenceEntityAssemblerTest {

    final CustomerPersistenceEntityAssembler assembler = new CustomerPersistenceEntityAssembler();

    @Test
    void shouldConvertFromDomain() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        CustomerPersistenceEntity customerPersistenceEntity = assembler.fromDomain(customer);

        Assertions.assertThat(customerPersistenceEntity).satisfies(
            p -> Assertions.assertThat(p.getId()).isEqualTo(customer.id().value()),
            p -> Assertions.assertThat(p.getFirstName()).isEqualTo(customer.fullName().firstName()),
            p -> Assertions.assertThat(p.getLastName()).isEqualTo(customer.fullName().lastName()),
            p -> Assertions.assertThat(p.getBirthDate()).isEqualTo(customer.birthDate().value()),
            p -> Assertions.assertThat(p.getEmail()).isEqualTo(customer.email().value()),
            p -> Assertions.assertThat(p.getPhone()).isEqualTo(customer.phone().value()),
            p -> Assertions.assertThat(p.getDocument()).isEqualTo(customer.document().value()),
            p -> Assertions.assertThat(p.getPromotionNotificationsAllowed()).isEqualTo(customer.isPromotionNotificationsAllowed()),
            p -> Assertions.assertThat(p.getArchived()).isEqualTo(customer.isArchived()),
            p -> Assertions.assertThat(p.getRegisteredAt()).isEqualTo(customer.registeredAt()),
            p -> Assertions.assertThat(p.getArchivedAt()).isEqualTo(customer.archivedAt()),
            p -> Assertions.assertThat(p.getLoyaltyPoints()).isEqualTo(customer.loyaltyPoints().value())
        );

        Address customerAddress = customer.address();
        Assertions.assertThat(customerPersistenceEntity.getAddress()).satisfies(
            p -> Assertions.assertThat(p.getStreet()).isEqualTo(customerAddress.street()),
            p -> Assertions.assertThat(p.getComplement()).isEqualTo(customerAddress.complement()),
            p -> Assertions.assertThat(p.getNeighborhood()).isEqualTo(customerAddress.neighborhood()),
            p -> Assertions.assertThat(p.getNumber()).isEqualTo(customerAddress.number()),
            p -> Assertions.assertThat(p.getCity()).isEqualTo(customerAddress.city()),
            p -> Assertions.assertThat(p.getState()).isEqualTo(customerAddress.state()),
            p -> Assertions.assertThat(p.getZipCode()).isEqualTo(customerAddress.zipCode().value())
        );
    }

}