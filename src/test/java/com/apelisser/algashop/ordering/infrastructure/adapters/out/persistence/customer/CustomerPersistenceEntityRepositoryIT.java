package com.apelisser.algashop.ordering.infrastructure.adapters.out.persistence.customer;

import com.apelisser.algashop.ordering.infrastructure.adapters.out.persistence.AbstractPersistenceIT;
import com.apelisser.algashop.ordering.infrastructure.config.auditing.SpringDataAuditingConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import(SpringDataAuditingConfig.class)
class CustomerPersistenceEntityRepositoryIT extends AbstractPersistenceIT {

    final CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

    @Autowired
    CustomerPersistenceEntityRepositoryIT(CustomerPersistenceEntityRepository customerPersistenceEntityRepository) {
        this.customerPersistenceEntityRepository = customerPersistenceEntityRepository;
    }

    @Test
    void shouldPersist() {
        CustomerPersistenceEntity entity = CustomerPersistenceEntityTestDataBuilder.existingCustomer().build();

        customerPersistenceEntityRepository.saveAndFlush(entity);
        Assertions.assertThat(customerPersistenceEntityRepository.existsById(entity.getId())).isTrue();

        CustomerPersistenceEntity savedEntity = customerPersistenceEntityRepository.findById(entity.getId()).orElseThrow();
        Assertions.assertThat(savedEntity.getAddress()).isNotNull();
    }

    @Test
    void shouldCount() {
        long ordersCount = customerPersistenceEntityRepository.count();
        Assertions.assertThat(ordersCount).isZero();
    }

    @Test
    void shouldSetAuditingValues() {
        CustomerPersistenceEntity entity = CustomerPersistenceEntityTestDataBuilder.existingCustomer().build();
        entity = customerPersistenceEntityRepository.saveAndFlush(entity);

        Assertions.assertThat(entity).satisfies(
            s -> Assertions.assertThat(s.getCreatedByUserId()).isNotNull(),
            s -> Assertions.assertThat(s.getLastModifiedAt()).isNotNull(),
            s -> Assertions.assertThat(s.getLastModifiedByUserId()).isNotNull()
        );
    }

}