package com.apelisser.algashop.ordering.infrastructure.persistence.provider;

import com.apelisser.algashop.ordering.domain.model.customer.Customer;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.apelisser.algashop.ordering.domain.model.commons.Email;
import com.apelisser.algashop.ordering.infrastructure.persistence.assembler.CustomerPersistenceEntityAssembler;
import com.apelisser.algashop.ordering.infrastructure.persistence.config.SpringDataAuditingConfig;
import com.apelisser.algashop.ordering.infrastructure.persistence.disassembler.CustomerPersistenceEntityDisassembler;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({
    CustomersPersistenceProvider.class,
    CustomerPersistenceEntityAssembler.class,
    CustomerPersistenceEntityDisassembler.class,
    SpringDataAuditingConfig.class
})
class CustomersPersistenceProviderIT {

    private final CustomersPersistenceProvider customersPersistenceProvider;

    @Autowired
    public CustomersPersistenceProviderIT(CustomersPersistenceProvider customersPersistenceProvider) {
        this.customersPersistenceProvider = customersPersistenceProvider;
    }

    @Test
    void givenANewCustomer_shouldPersist() {
        Customer newCustomer = CustomerTestDataBuilder.brandNewCustomer().build();

        customersPersistenceProvider.add(newCustomer);

        Customer persistedCustomer = customersPersistenceProvider.ofId(newCustomer.id()).orElseThrow();

        Assertions.assertThat(persistedCustomer).satisfies(p -> {
            Assertions.assertThat(p.id()).isEqualTo(newCustomer.id());
            Assertions.assertThat(p.fullName()).isEqualTo(newCustomer.fullName());
            Assertions.assertThat(p.birthDate()).isEqualTo(newCustomer.birthDate());
            Assertions.assertThat(p.email()).isEqualTo(newCustomer.email());
            Assertions.assertThat(p.phone()).isEqualTo(newCustomer.phone());
            Assertions.assertThat(p.document()).isEqualTo(newCustomer.document());
            Assertions.assertThat(p.isPromotionNotificationsAllowed()).isEqualTo(newCustomer.isPromotionNotificationsAllowed());
            Assertions.assertThat(p.isArchived()).isEqualTo(newCustomer.isArchived());
            Assertions.assertThat(p.registeredAt()).isEqualTo(newCustomer.registeredAt());
            Assertions.assertThat(p.archivedAt()).isEqualTo(newCustomer.archivedAt());
            Assertions.assertThat(p.loyaltyPoints()).isEqualTo(newCustomer.loyaltyPoints());
        });
    }

    @Test
    void givenANewPersistedCustomer_whenUpdate_shouldIncrementVersion() {
        Customer newCustomer = CustomerTestDataBuilder.brandNewCustomer().build();
        customersPersistenceProvider.add(newCustomer);
        Customer existingCustomer = customersPersistenceProvider.ofId(newCustomer.id()).orElseThrow();
        long previousVersion = existingCustomer.version();

        existingCustomer.changeEmail(new Email("new.email@example.com"));
        customersPersistenceProvider.add(existingCustomer);

        Customer updatedCustomer = customersPersistenceProvider.ofId(newCustomer.id()).orElseThrow();

        Assertions.assertThat(updatedCustomer.version()).isGreaterThan(previousVersion);

    }
}